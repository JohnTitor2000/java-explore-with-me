package ru.practicum.dto.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateCommentDto {
    @NotNull
    @NotBlank
    private String content;
}
