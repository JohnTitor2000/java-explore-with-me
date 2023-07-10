package ru.practicum.dto.compilation;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class UpdateCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @Size(max = 50)
    private String title;
}