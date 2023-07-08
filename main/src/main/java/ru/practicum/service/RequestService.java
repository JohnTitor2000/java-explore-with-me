package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.RequestOutputDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.Status;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RequestService {

    ParticipationRequestRepository prRepository;
    EventRepository eventRepository;
    UserRepository userRepository;

    public RequestOutputDto addRequest(Long userId, Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        } else if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        ParticipationRequest participationRequest = prRepository.save(RequestMapper.toParticipationRequest(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found")),
                eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"))));
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
        participationRequest.setStatus(Status.REJECTED);

        return RequestMapper.toRequestOutputDto(prRepository.save(participationRequest));
    }
}
