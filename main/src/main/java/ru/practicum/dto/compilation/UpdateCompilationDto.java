package ru.practicum.dto.compilation;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class UpdateCompilationDto {
    List<Long> events;
    Boolean pinned;
    @Size(max = 50)
    String title;
}