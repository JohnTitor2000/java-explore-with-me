package ru.practicum.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticDto;
import ru.practicum.mappers.EventMapper;
import ru.practicum.model.Event;
import ru.prakticum.client.StatClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private final StatClient statClient = new StatClient();
    DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    void setStatistic(List<Event> events) {
        if (events.isEmpty()) {
            return;
        }
        List<String> uris = events.stream().map(EventMapper::toUri).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        List<StatisticDto> statisticsDto = statClient.getStatistic(LocalDateTime.now().minusYears(2).format(dtFormatter), now.format(dtFormatter), uris, true);
        if (statisticsDto.isEmpty()) {
            events.stream().forEach(o -> o.setViews(0L));
        }
        for (StatisticDto statisticDto : statisticsDto) {
            Long id = Long.parseLong(String.valueOf(statisticDto.getUri().charAt(statisticDto.getUri().length() - 1)));
            for (Event event : events) {
                if(Objects.equals(event.getId(), id)) {
                    event.setViews(statisticDto.getHits());
                }
            }
        }
    }

    void setStatistic(Event event) {
        String uris = EventMapper.toUri(event);
        LocalDateTime now = LocalDateTime.now();
        List<StatisticDto> statisticsDto = statClient.getStatistic(LocalDateTime.now().minusYears(2).format(dtFormatter), now.format(dtFormatter), Collections.singletonList(uris), true);
        if (statisticsDto.isEmpty()) {
            event.setViews(0L);
        } else {
            event.setViews(statisticsDto.get(0).getHits());
        }
    }

    void addHit(String uri, String app, HttpServletRequest httpServletRequest) {
        LocalDateTime now = LocalDateTime.now();
        HitDto hitDto = new HitDto(app, uri, httpServletRequest.getRemoteAddr(), now.format(dtFormatter));
        statClient.addHit(hitDto);
    }
}
