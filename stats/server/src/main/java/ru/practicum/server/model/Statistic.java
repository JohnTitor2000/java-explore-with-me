package ru.practicum.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Statistic {
    private String app;
    private String uri;
    private Long hits;
}
