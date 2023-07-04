package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.*;
import ru.practicum.exception.BadRequest;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictExeption;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    public List<EventDataDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                 Integer size, HttpServletRequest httpServletRequest) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd) || rangeEnd.isBefore(LocalDateTime.now())) {
                throw new BadRequest("Начало и окончание события должны быть в правильном хронологическом порядке.");
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
        }
        return events.stream().map(o -> EventMapper.toEventDataDto(o, participationRequestRepository.getConfirmedRequestsByEventId(o.getId()))).collect(Collectors.toList());
    }

    public FullEventDto getEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
        statisticService.setStatistic(event);
        return EventMapper.toFullEventDto(event, participationRequestRepository.getConfirmedRequestsByEventId(id));
    }

    public List<FullEventDto> getEventsByUserId(List<Long> usersIds, List<String> states, List<Long> categories,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<State> statesResult = states.stream().map(State::stateFromString).collect(Collectors.toList());
        List<Event> events = eventRepository.getEventsByUserId(usersIds, statesResult, categories, rangeStart, rangeEnd, pageable);
        statisticService.setStatistic(events);
        return events.stream().map(o -> EventMapper.toFullEventDto(o, participationRequestRepository.getConfirmedRequestsByEventId(o.getId()))).collect(Collectors.toList());
    }

    public EventDataDto updateEvent(InputUpdateEventDto event, Long eventId) {
        Event modifiedEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if(LocalDateTime.now().plusHours(1).isAfter(modifiedEvent.getEventDate())) {
            throw new ConflictExeption("Cannot publish the event because it's not in the right state: PUBLISHED");
        }
        if(!modifiedEvent.getState().equals(State.PENDING)) {
            throw new ConflictExeption("Cannot publish the event because it's not in the right state: " + modifiedEvent.getState());
        }
        if (event.getTitle() != null) modifiedEvent.setTitle(event.getTitle());
        if (event.getAnnotation() != null) modifiedEvent.setAnnotation(event.getAnnotation());
        if (event.getRequestModeration() != null) modifiedEvent.setRequestModeration(event.getRequestModeration());
        if (event.getCategory() != null) modifiedEvent.setCategory(categoryRepository.findById(event.getCategory()).
                orElseThrow(() -> new NotFoundException("category with id=" + event.getCategory() + " not found")));
        if (event.getDescription() != null) modifiedEvent.setDescription(event.getDescription());
        if (event.getLocation() != null) modifiedEvent.setLocation(event.getLocation());
        if (event.getParticipantLimit() != null) modifiedEvent.setParticipantLimit(event.getParticipantLimit());
        if (event.getEventDate() != null) modifiedEvent.setEventDate(event.getEventDate());
        if (event.getPaid() != null) modifiedEvent.setPaid(event.getPaid());
        if (event.getStateAction() != null) {
            if (event.getStateAction().equals("PUBLISH_EVENT")) {
                modifiedEvent.setState(State.PUBLISHED);
            } else {
                modifiedEvent.setState(State.CANCELED);
            }
        }
        return EventMapper.toEventDataDto(eventRepository.save(modifiedEvent), participationRequestRepository.getConfirmedRequestsByEventId(eventId));
    }

    public List<EventDataDto> getEventsByUser(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw  new NotFoundException("User not found");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        statisticService.setStatistic(events);
        return events.stream().map(o -> EventMapper.toEventDataDto(o, participationRequestRepository.getConfirmedRequestsByEventId(o.getId()))).collect(Collectors.toList());
    }

    public EventDataDto createEvent(Long userId, InputNewEventDto inputNewEventDto) {
        Event event = EventMapper.toEvent(inputNewEventDto, categoryRepository.findById(inputNewEventDto.getCategory()).get(),
                                        userRepository.findById(userId).get());
        Event createdEvent = eventRepository.save(event);
        statisticService.setStatistic(event);
        return EventMapper.toEventDataDto(createdEvent, participationRequestRepository.getConfirmedRequestsByEventId(createdEvent.getId()));
    }

    public FullEventDto getFullEvent(Long userId, Long eventId) {
        if (!eventRepository.existsById(eventId) || !eventRepository.findById(eventId).get().getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        Event event = eventRepository.findById(eventId).get();
        statisticService.setStatistic(event);
        return EventMapper.toFullEventDto(event, participationRequestRepository.getConfirmedRequestsByEventId(eventId));
    }

    public FullEventDto updateEventByUser(Long userId, Long eventId, InputUpdateEventFromUserDto inputUpdateEventFromUserDto) {
        Event modifiedEvent = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!modifiedEvent.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        if(LocalDateTime.now().plusHours(2).isAfter(modifiedEvent.getEventDate())) {
            throw new ConflictExeption("Cannot publish the event because it's not in the right state: PUBLISHED");
        }
        if(modifiedEvent.getState().equals(State.PUBLISHED)) {
            throw new BadRequest("Only pending or canceled events can be changed");
        }
        if (inputUpdateEventFromUserDto.getTitle() != null) modifiedEvent.setTitle(inputUpdateEventFromUserDto.getTitle());
        if (inputUpdateEventFromUserDto.getAnnotation() != null) modifiedEvent.setAnnotation(inputUpdateEventFromUserDto.getAnnotation());
        if (inputUpdateEventFromUserDto.getRequestModeration() != null) modifiedEvent.setRequestModeration(inputUpdateEventFromUserDto.getRequestModeration());
        if (inputUpdateEventFromUserDto.getCategory() != null) modifiedEvent.setCategory(categoryRepository.findById(inputUpdateEventFromUserDto.getCategory()).
                orElseThrow(() -> new NotFoundException("category with id=" + inputUpdateEventFromUserDto.getCategory() + " not found")));
        if (inputUpdateEventFromUserDto.getDescription() != null) modifiedEvent.setDescription(inputUpdateEventFromUserDto.getDescription());
        if (inputUpdateEventFromUserDto.getLocation() != null) modifiedEvent.setLocation(inputUpdateEventFromUserDto.getLocation());
        if (inputUpdateEventFromUserDto.getParticipantLimit() != null) modifiedEvent.setParticipantLimit(inputUpdateEventFromUserDto.getParticipantLimit());
        if (inputUpdateEventFromUserDto.getEventDate() != null) modifiedEvent.setEventDate(inputUpdateEventFromUserDto.getEventDate());
        if (inputUpdateEventFromUserDto.getPaid() != null) modifiedEvent.setPaid(inputUpdateEventFromUserDto.getPaid());
        if (inputUpdateEventFromUserDto.getStateAction().equals("CANCEL_REVIEW")) {
            modifiedEvent.setState(State.PENDING);
        }
        return EventMapper.toFullEventDto(eventRepository.save(modifiedEvent), participationRequestRepository.getConfirmedRequestsByEventId(eventId));
    }


    public RequestOutputDto getRequestByUserId(Long userId, Long eventId) {
        ParticipationRequest pr = participationRequestRepository.getRequestByUserIdAndEventId(userId, eventId);
        return RequestMapper.toRequestOutputDto(pr);
    }

    public List<RequestOutputDto> updateRequests(Long userId, Long eventId, ConfirmRequestDto confirmRequestDto) {
        if (!eventRepository.existsById(eventId) || !eventRepository.findById(eventId).get().getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        List<ParticipationRequest> prs = participationRequestRepository.getRequestsByRequestIds(confirmRequestDto.getRequestIds());
        for (ParticipationRequest pr : prs) {
            if (!pr.getStatus().equals(Status.PENDING)) {
                throw  new BadRequestException("Request must have status PENDING");
            }
        }
        prs.forEach(o -> o.setStatus(Status.statusFromString(confirmRequestDto.getStatus())));
        return prs.stream().map(RequestMapper::toRequestOutputDto).collect(Collectors.toList());
    }
}
