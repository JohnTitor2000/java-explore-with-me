package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.ConfirmRequestDto;
import ru.practicum.dto.request.RequestOutputDto;
import ru.practicum.dto.request.RequestResultUpdateDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictExeption;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EventService {


    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final StatisticService statisticService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private LocationRepository locationRepository;

    public List<EventDataDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                        Integer size, HttpServletRequest httpServletRequest) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd) || rangeEnd.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Начало и окончание события должны быть в правильном хронологическом порядке.");
            }
        }
        List<Event> events;
        if (onlyAvailable) {
            events = eventRepository.getAvailableEvents(text, State.PUBLISHED, categories, paid, rangeStart, rangeEnd, PageRequest.of(from, size));
        } else {
            events = eventRepository.getAllEvents(text, State.PUBLISHED, categories, paid, rangeStart, rangeEnd, PageRequest.of(from, size));
        }
        statisticService.setStatistic(events);
        if (sort == null || !sort.equals("VIEWS")) {
            events.sort(Comparator.comparing(Event::getEventDate));
        } else {
            events.sort(Comparator.comparing(Event::getViews));
        }
        int resultFrom = from.equals(0) ? 0 : from - 1;
        events = events.stream().skip(resultFrom).limit(size).collect(Collectors.toList());
        for (Event event : events) {
            statisticService.addHit(EventMapper.toUri(event), "ewm-main-service", httpServletRequest);
            event.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(event.getId()));
        }
        return events.stream().map(EventMapper::toEventDataDto).collect(Collectors.toList());
    }

    public FullEventDto getEventById(Long id, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + id + " was not found");
        }
        statisticService.setStatistic(event);
        statisticService.addHit(EventMapper.toUri(event), "ewm-main-service", httpServletRequest);
        event.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(event.getId()));
        return EventMapper.toFullEventDto(event);
    }

    public List<FullEventDto> getEventsByUserId(List<Long> usersIds, List<String> states, List<Long> categories,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<State> statesResult;
        if (states != null) {
            statesResult = states.stream().map(State::stateFromString).collect(Collectors.toList());
        } else {
            statesResult = null;
        }
        List<Event> events = eventRepository.getEventsByUserId(usersIds, statesResult, categories, rangeStart, rangeEnd, pageable);
        statisticService.setStatistic(events);
        events.forEach(o -> o.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(o.getId())));
        return events.stream().map(EventMapper::toFullEventDto).collect(Collectors.toList());
    }

    public FullEventDto updateEvent(InputUpdateEventDto event, Long eventId) {
        Event modifiedEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getStateAction() != null && event.getStateAction().equals("PUBLISH_EVENT")) {
            if (modifiedEvent.getState() != State.PENDING) {
                throw new ConflictExeption("Не возможно опубликовать событие с ИД: " + eventId);
            }
            LocalDateTime published = LocalDateTime.now();
            modifiedEvent.setPublishedOn(published);
            modifiedEvent.setState(State.PUBLISHED);
        }

        if (event.getStateAction() != null && event.getStateAction().equals("REJECT_EVENT")) {
            if (modifiedEvent.getState() == State.PUBLISHED && modifiedEvent.getPublishedOn().isBefore(LocalDateTime.now())) {
                throw new ConflictExeption("Не возможно опубликовать событие с ИД: " + eventId);
            }
            modifiedEvent.setState(State.CANCELED);
        }
        if (event.getTitle() != null) {
            modifiedEvent.setTitle(event.getTitle());
        }
        if (event.getAnnotation() != null) {
            modifiedEvent.setAnnotation(event.getAnnotation());
        }
        if (event.getRequestModeration() != null) {
            modifiedEvent.setRequestModeration(event.getRequestModeration());
        }

        if (event.getCategory() != null) {
            modifiedEvent.setCategory(categoryRepository.findById(event.getCategory())
                            .orElseThrow(() -> new NotFoundException("category with id=" + event.getCategory() + " not found")));
        }
        if (event.getDescription() != null) {
            modifiedEvent.setDescription(event.getDescription());
        }
        if (event.getParticipantLimit() != null) {
            modifiedEvent.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getEventDate() != null) {
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Не верная дата события");
            }
            modifiedEvent.setEventDate(event.getEventDate());
        }
        if (event.getPaid() != null) {
            modifiedEvent.setPaid(event.getPaid());
        }
        modifiedEvent.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(modifiedEvent.getId()));
        return EventMapper.toFullEventDto(eventRepository.save(modifiedEvent));
    }

    public List<EventDataDto> getEventsByUser(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw  new NotFoundException("User not found");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        statisticService.setStatistic(events);
        events.stream().forEach(o -> o.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(o.getId())));
        return events.stream().map(EventMapper::toEventDataDto).collect(Collectors.toList());
    }

    public FullEventDto createEvent(Long userId, InputNewEventDto inputNewEventDto) {
        if (inputNewEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента.");
        }
        Event event = EventMapper.toEvent(inputNewEventDto, categoryRepository.findById(inputNewEventDto.getCategory()).get(),
                                        userRepository.findById(userId).get());
        locationRepository.save(event.getLocation());
        Event createdEvent = eventRepository.save(event);
        event.setViews(0L);
        event.setConfirmedRequest(0);
        return EventMapper.toFullEventDto(createdEvent);
    }

    public FullEventDto getFullEvent(Long userId, Long eventId) {
        if (!eventRepository.existsById(eventId) || !eventRepository.findById(eventId).get().getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        Event event = eventRepository.findById(eventId).get();
        statisticService.setStatistic(event);
        event.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(event.getId()));
        return EventMapper.toFullEventDto(event);
    }

    public FullEventDto updateEventByUser(Long userId, Long eventId, InputUpdateEventFromUserDto inputUpdateEventFromUserDto) {
        Event modifiedEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!modifiedEvent.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        if (modifiedEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictExeption("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }
        if (inputUpdateEventFromUserDto.getEventDate() != null) {
            if (inputUpdateEventFromUserDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            }
        }
        if (inputUpdateEventFromUserDto.getStateAction() != null && inputUpdateEventFromUserDto.getStateAction().equals("SEND_TO_REVIEW")) {
            modifiedEvent.setState(State.PENDING);
        }
        if (inputUpdateEventFromUserDto.getStateAction() != null && inputUpdateEventFromUserDto.getStateAction().equals("CANCEL_REVIEW")) {
            modifiedEvent.setState(State.CANCELED);
        }
        if (inputUpdateEventFromUserDto.getTitle() != null) {
            modifiedEvent.setTitle(inputUpdateEventFromUserDto.getTitle());
        }
        if (inputUpdateEventFromUserDto.getAnnotation() != null) {
            modifiedEvent.setAnnotation(inputUpdateEventFromUserDto.getAnnotation());
        }
        if (inputUpdateEventFromUserDto.getRequestModeration() != null) {
            modifiedEvent.setRequestModeration(inputUpdateEventFromUserDto.getRequestModeration());
        }
        if (inputUpdateEventFromUserDto.getCategory() != null) {
            modifiedEvent.setCategory(categoryRepository.findById(inputUpdateEventFromUserDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("category with id=" + inputUpdateEventFromUserDto.getCategory() + " not found")));
        }
        if (inputUpdateEventFromUserDto.getDescription() != null && !inputUpdateEventFromUserDto.getDescription().isBlank()) {
            modifiedEvent.setDescription(inputUpdateEventFromUserDto.getDescription());
        }
        if (inputUpdateEventFromUserDto.getLocation() != null) {
            modifiedEvent.setLocation(inputUpdateEventFromUserDto.getLocation());
        }
        if (inputUpdateEventFromUserDto.getParticipantLimit() != null) {
            modifiedEvent.setParticipantLimit(inputUpdateEventFromUserDto.getParticipantLimit());
        }
        if (inputUpdateEventFromUserDto.getPaid() != null) {
            modifiedEvent.setPaid(inputUpdateEventFromUserDto.getPaid());
        }
        eventRepository.save(modifiedEvent);
        modifiedEvent.setConfirmedRequest(participationRequestRepository.getConfirmedRequestsByEventId(modifiedEvent.getId()));
        return EventMapper.toFullEventDto(modifiedEvent);
    }


    public List<RequestOutputDto> getRequestByUserId(Long userId, Long eventId) {
        List<ParticipationRequest> pr = participationRequestRepository.getRequestByUserIdAndEventId(userId, eventId);
        if (pr == null) {
            throw new NotFoundException("request not found");
        }
        return pr.stream().map(RequestMapper::toRequestOutputDto).collect(Collectors.toList());
    }

    public RequestResultUpdateDto updateRequests(Long userId, Long eventId, ConfirmRequestDto confirmRequestDto) {
        if (!eventRepository.existsById(eventId) || !eventRepository.findById(eventId).get().getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        List<ParticipationRequest> prs = participationRequestRepository.getRequestsByRequestIds(confirmRequestDto.getRequestIds());
        Event event = eventRepository.findById(eventId).get();
        Integer freePlaces = event.getParticipantLimit() - participationRequestRepository.getConfirmedRequestsByEventId(eventId);
        if (freePlaces <= 0) {
            throw new ConflictExeption("Have not free places");
        }
        for (ParticipationRequest pr : prs) {
            if (!pr.getStatus().equals(Status.PENDING)) {
                throw  new ConflictExeption("Request must have status PENDING");
            }
        }
        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        if (confirmRequestDto.getStatus().equals("CONFIRMED")) {
            for (int i = 0; i < prs.size(); i++) {
                if(i >= freePlaces) {
                    prs.get(i).setStatus(Status.REJECTED);
                    rejectedRequests.add(prs.get(i));
                } else {
                    prs.get(i).setStatus(Status.CONFIRMED);
                    confirmedRequests.add(prs.get(i));
                }
            }
            confirmedRequests.forEach(participationRequestRepository::save);
            rejectedRequests.forEach(participationRequestRepository::save);
            RequestResultUpdateDto result = new RequestResultUpdateDto();
            result.setConfirmedRequests(confirmedRequests.stream().map(RequestMapper::toRequestOutputDto).collect(Collectors.toList()));
            result.setRejectedRequests(rejectedRequests.stream().map(RequestMapper::toRequestOutputDto).collect(Collectors.toList()));
            return result;
        } else {
            prs.forEach(o -> o.setStatus(Status.REJECTED));
            prs.forEach(participationRequestRepository::save);
            rejectedRequests.addAll(prs);
            RequestResultUpdateDto result = new RequestResultUpdateDto();
            result.setRejectedRequests(rejectedRequests.stream().map(RequestMapper::toRequestOutputDto).collect(Collectors.toList()));
            result.setConfirmedRequests(Collections.emptyList());
            return result;
        }
    }
}
