package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.RequestOutputDto;
import ru.practicum.exception.ConflictExeption;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.State;
import ru.practicum.model.Status;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RequestService {

    private final ParticipationRequestRepository prRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RequestOutputDto addRequest(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictExeption("Initiator cant send request to his own event");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictExeption("Event with id=" + eventId + " was not found");
        }
        if (event.getParticipantLimit() > 0 && prRepository.getConfirmedRequestsByEventId(eventId) >= event.getParticipantLimit()) {
            throw new ConflictExeption("Have not free places");
        }
        ParticipationRequest participationRequest = prRepository.save(RequestMapper.toParticipationRequest(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found")), event));
        return RequestMapper.toRequestOutputDto(participationRequest);
    }

    public List<RequestOutputDto> getRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        return prRepository.findByRequesterId(userId).stream().map(RequestMapper::toRequestOutputDto).collect(Collectors.toList());
    }

    public RequestOutputDto cancelRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        if (!prRepository.existsById(requestId)) {
            throw new NotFoundException("Request with id=" + requestId + " was not found");
        }
        ParticipationRequest participationRequest = prRepository.findById(requestId).get();
        participationRequest.setStatus(Status.CANCELED);

        return RequestMapper.toRequestOutputDto(prRepository.save(participationRequest));
    }
}
