package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.Category;
import ru.practicum.model.Location;
import ru.practicum.model.State;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class FullEventDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private Category category;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private User initiator;
    private Boolean paid;
    private Integer participantLimit;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private State state;
    private Long views;
    private Integer confirmedRequests;
    private Location location;
}
