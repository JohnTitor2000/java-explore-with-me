package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class NewCategoryDto {
    @Size(min = 1, max = 50)
    String name;
}
