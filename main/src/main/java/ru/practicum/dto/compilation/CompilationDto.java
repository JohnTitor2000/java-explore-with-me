package ru.practicum.dto.compilation;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.event.FullEventDto;

import java.util.Set;

@Getter
@Setter
public class CompilationDto {
    private Set<FullEventDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}