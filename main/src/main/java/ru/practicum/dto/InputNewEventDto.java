package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
public class InputNewEventDto {
    private String annotation;
    private Long category;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private Integer participantLimit;
    private boolean requestModeration;
    private String title;
}
