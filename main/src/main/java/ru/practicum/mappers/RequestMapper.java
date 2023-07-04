package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.RequestOutputDto;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.Status;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class RequestMapper {

    public ParticipationRequest toParticipationRequest(User user, Event event) {
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(Status.PENDING);
        participationRequest.setRequester(user);
        participationRequest.setEvent(event);
        return participationRequest;
    }

    public RequestOutputDto toRequestOutputDto(ParticipationRequest participationRequest) {
        RequestOutputDto requestOutputDto = new RequestOutputDto();
        requestOutputDto.setId(participationRequest.getId());
        requestOutputDto.setStatus(participationRequest.getStatus());
        requestOutputDto.setRequester(participationRequest.getRequester().getId());
        requestOutputDto.setEvent(participationRequest.getEvent().getId());
        requestOutputDto.setCreated(participationRequest.getCreated());
        return requestOutputDto;
    }
}
