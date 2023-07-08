package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.model.Compilation;

import java.util.Collections;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            compilationDto.setEvents(compilation.getEvents().stream().map(EventMapper::toFullEventDto).collect(Collectors.toSet()));
        } else {
            compilationDto.setEvents(Collections.EMPTY_SET);
        }
        return compilationDto;
    }
}
