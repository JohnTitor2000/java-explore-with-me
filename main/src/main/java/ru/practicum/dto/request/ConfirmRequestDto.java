package ru.practicum.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfirmRequestDto {
    private List<Long> requestIds;
    private String status;
}
