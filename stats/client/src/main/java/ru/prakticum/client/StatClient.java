package ru.prakticum.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticDto;

import java.time.LocalDateTime;
import java.util.List;

public class StatClient {

    private static final String BASE_URL = "http://localhost:9090";

    WebClient webClient = WebClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public void addHit(HitDto hitDto) {
        Mono<String> postResponse = webClient
                .post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(hitDto)
                .retrieve()
                .bodyToMono(String.class);
        String postResponseBody = postResponse.block();
    }

    public List<StatisticDto> getStatistic(String start, String end, List<String> uris, boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats") // Замените "/stats" на фактический путь к вашему контроллеру
                        .queryParam("start", start) // Установите фактические значения параметров start, end, uris и unique
                        .queryParam("end", end)
                        .queryParam("uris", uris.toArray())
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToFlux(StatisticDto.class)
                .collectList()
                .block();
    }
}
