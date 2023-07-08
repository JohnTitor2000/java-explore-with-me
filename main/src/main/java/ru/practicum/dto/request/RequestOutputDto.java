package ru.practicum.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.Status;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequestOutputDto {
    Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
    Long event;
    Long requester;
    Status status;
}
