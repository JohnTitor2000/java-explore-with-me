package ru.practicum.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestResultUpdateDto {
    private List<RequestOutputDto> confirmedRequests;
    private List<RequestOutputDto> rejectedRequests;
}
