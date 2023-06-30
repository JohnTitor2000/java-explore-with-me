package ru.practicum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
public class Event {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private Integer confirmedRequests;
    private String createdOn;
    private String eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private State state;
    private Integer views;
}
