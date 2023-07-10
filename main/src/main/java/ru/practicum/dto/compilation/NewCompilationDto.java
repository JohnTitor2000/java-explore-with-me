package ru.practicum.dto.compilation;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned = false;
    @Size(max = 50)
    @NotNull
    @NotBlank
    private String title;
}
