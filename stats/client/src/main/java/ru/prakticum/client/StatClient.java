package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticDto;
import ru.prakticum.client.BaseClient;

import java.util.List;
import java.util.Map;

import static ru.practicum.dto.Constants.HIT_URI;
import static ru.practicum.dto.Constants.STATS_URI;

@Service
public class StatClient extends BaseClient {
    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(HitDto hitDto) {
        return post(HIT_URI, hitDto);
    }

    public ResponseEntity<Object> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        if (unique != null) {
            Map<String, Object> params = Map.of(
                    "start", start,
                    "end", end,
                    "uris", uris,
                    "unique", unique
            );
            return get(STATS_URI + "?start={start}&end={end}&uris={uris}&unique={unique}", params);
        }
        Map<String, Object> params = Map.of(
                "start",start,
                "end", end,
                "uris", uris
        );
        return get(STATS_URI + "?start={start}&end={end}&uris={uris}", params);
    }
}