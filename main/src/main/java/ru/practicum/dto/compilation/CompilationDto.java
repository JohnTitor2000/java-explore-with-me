package ru.practicum.dto.compilation;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.event.FullEventDto;

import java.util.Set;

@Getter
@Setter
public class CompilationDto {
    Set<FullEventDto> events;
    Long id;
    Boolean pinned;
    String title;
}