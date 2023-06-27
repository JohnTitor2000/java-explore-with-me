package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class HitDto {
    @NotBlank
    @NotNull
    String app;
    @NotBlank
    @NotNull
    String uri;
    @NotBlank
    @NotNull
    String ip;
    @NotNull
    String timestamp;
}
