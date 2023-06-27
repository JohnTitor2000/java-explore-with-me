package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class ShortUser {
    private String name;
    private String email;
}
