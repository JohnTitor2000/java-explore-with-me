package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.Category;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventDataDto {
    private String annotation;
    private Category category;
    private Integer confirmedRequests;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Long id;
    private User initiator;
    private boolean paid;
    private String title;
    private Long views;
}
