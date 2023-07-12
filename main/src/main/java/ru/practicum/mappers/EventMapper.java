package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.event.EventDataDto;
import ru.practicum.dto.event.FullEventDto;
import ru.practicum.dto.event.InputNewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public String toUri(Event event) {
        String uri = "/events/" + event.getId();
        return uri;
    }

    public EventDataDto toEventDataDto(Event event) {
        EventDataDto eventDataDto = new EventDataDto();
        eventDataDto.setTitle(event.getTitle());
        eventDataDto.setId(event.getId());
        eventDataDto.setCategory(event.getCategory());
        eventDataDto.setEventDate(event.getEventDate());
        eventDataDto.setAnnotation(event.getAnnotation());
        eventDataDto.setViews(event.getViews());
        eventDataDto.setInitiator(event.getInitiator());
        eventDataDto.setPaid(event.getPaid());
        eventDataDto.setConfirmedRequests(event.getConfirmedRequest());
        return eventDataDto;
    }

    public FullEventDto toFullEventDto(Event event) {
        FullEventDto fullEventDto = new FullEventDto();
        fullEventDto.setId(event.getId());
        fullEventDto.setTitle(event.getTitle());
        fullEventDto.setAnnotation(event.getAnnotation());
        fullEventDto.setDescription(event.getDescription());
        fullEventDto.setCategory(event.getCategory());
        fullEventDto.setCreatedOn(event.getCreatedOn());
        fullEventDto.setEventDate(event.getEventDate());
        fullEventDto.setInitiator(event.getInitiator());
        fullEventDto.setPaid(event.getPaid());
        fullEventDto.setParticipantLimit(event.getParticipantLimit());
        fullEventDto.setPublishedOn(event.getPublishedOn());
        fullEventDto.setRequestModeration(event.getRequestModeration());
        fullEventDto.setState(event.getState());
        fullEventDto.setViews(event.getViews());
        fullEventDto.setConfirmedRequests(event.getConfirmedRequest());
        fullEventDto.setLocation(event.getLocation());
        return fullEventDto;
    }

    public Event toEvent(InputNewEventDto inputNewEventDto, Category category, User user) {
        Event event = new Event();
        event.setTitle(inputNewEventDto.getTitle());
        event.setDescription(inputNewEventDto.getDescription());
        event.setAnnotation(inputNewEventDto.getAnnotation());
        event.setEventDate(inputNewEventDto.getEventDate());
        event.setPaid(inputNewEventDto.isPaid());
        event.setLocation(inputNewEventDto.getLocation());
        event.setParticipantLimit(inputNewEventDto.getParticipantLimit());
        event.setRequestModeration(inputNewEventDto.isRequestModeration());
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        event.setInitiator(user);
        return event;
    }
}
