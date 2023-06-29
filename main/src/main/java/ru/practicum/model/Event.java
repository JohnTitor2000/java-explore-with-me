package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.ShortUser;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class Event {
    Long id;
    String title;
    String annotation;
    String description;
    Category category;
    Integer confirmedRequests;
    String createdOn;
    String eventDate;
    ShortUser initiator;
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    State state;
    Integer views;
}
