package ru.practicum.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticDto;
import ru.practicum.server.service.StatisticService;

import javax.validation.Valid;

import java.util.List;

import static ru.practicum.dto.Constants.HIT_URI;
import static ru.practicum.dto.Constants.STATS_URI;

@Slf4j
@Validated
@RestController
public class StatisticController {

    StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @PostMapping(HIT_URI)
    @ResponseStatus(HttpStatus.CREATED)
    HitDto addHit(@RequestBody @Valid HitDto hitDto) {
        log.info("Static Controller get hit with app = {}, ip = {}, uri = {}, timestamp = {}",
                hitDto.getApp(), hitDto.getIp(), hitDto.getUri(), hitDto.getTimestamp());
        return statisticService.addHit(hitDto);
    }

    @GetMapping(STATS_URI)
    public List<StatisticDto> getStatistic(@RequestParam(required = false) String start,
                                           @RequestParam(required = false) String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get Statistic with param start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return statisticService.getStatistic(start, end, uris, unique);
    }
}
