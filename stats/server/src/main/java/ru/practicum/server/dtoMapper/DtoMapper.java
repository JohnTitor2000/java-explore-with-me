package ru.practicum.server.dtoMapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitDto;
import ru.practicum.server.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.dto.Constants.FORMAT;

@UtilityClass
public class DtoMapper {
    public Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setTimeStamp(LocalDateTime.parse(hitDto.getTimeStamp(), DateTimeFormatter.ofPattern(FORMAT)));
        return hit;
    }

    public HitDto toHitDto(Hit hit) {
        HitDto hitDto = new HitDto(hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimeStamp().toString());
        return hitDto;
    }
}
