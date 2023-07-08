package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.Category;

@UtilityClass
public class CategoryMapper {
    public Category toCategory (NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }
}
