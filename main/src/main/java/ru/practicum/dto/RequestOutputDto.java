package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.Status;

@Getter
@Setter
public class RequestOutputDto {
    Long id;
    String created;
    Long event;
    Long requester;
    Status status;
}
