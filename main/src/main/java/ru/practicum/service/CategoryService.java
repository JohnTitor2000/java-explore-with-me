package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mappers.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public List<Category> getCategories(Integer from, Integer size) {
        Integer resultFrom = from.equals(0) ? 1 : from - 1;
        return categoryRepository.findAll().stream().skip(from).limit(size).collect(Collectors.toList());
    }

    public Category getCategoriesById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with id=" + id + "was not found"));
    }

    public Category addCategory(NewCategoryDto newCategoryDto) {
        return categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
    }

    public void removeCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
        if (eventRepository.countEventsWithCategory(id) > 0) {
            throw new ConflictException("The category is not empty");
        }

        categoryRepository.deleteById(id);
    }

    public Category updateCategory(Long id, NewCategoryDto newCategoryDto) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
        Category category = CategoryMapper.toCategory(newCategoryDto);
        category.setId(id);
        return categoryRepository.save(category);
    }
}
