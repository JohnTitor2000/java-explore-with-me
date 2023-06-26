package ru.practicum.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticDto;
import ru.practicum.server.dtoMapper.DtoMapper;
import ru.practicum.server.model.Hit;
import ru.practicum.server.repository.StatisticRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static ru.practicum.dto.Constants.FORMAT;

@Slf4j
@Service
public class StatisticService {
    StatisticRepository statisticRepository;

    @Autowired
    public StatisticService(StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    @Transactional
    public HitDto addHit(HitDto hitDto) {
        Hit createdHit = statisticRepository.save(DtoMapper.toHit(hitDto));
        log.info("Created hit with id = {}", createdHit.getId());
        return DtoMapper.toHitDto(createdHit);
    }

    public List<StatisticDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        List<String> resultUri = uris == null ? Collections.emptyList() : uris;
        if (unique) {
            return statisticRepository.getStatisticUnique(LocalDateTime.parse(start, DateTimeFormatter.ofPattern(FORMAT)),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern(FORMAT)), resultUri, resultUri.isEmpty());
        }
        return statisticRepository.getStatisticNotUnique(LocalDateTime.parse(start, DateTimeFormatter.ofPattern(FORMAT)),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern(FORMAT)), resultUri, resultUri.isEmpty());
    }
}
