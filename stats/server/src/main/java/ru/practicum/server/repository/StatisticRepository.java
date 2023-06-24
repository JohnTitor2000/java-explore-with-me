package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatisticDto;
import ru.practicum.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT new ru.practicum.dto.StatisticDto(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM Hit as hit " +
            "WHERE hit.timeStamp between :start and :end " +
            "AND (:isEmpty = true OR hit.uri IN (:uris)) " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY COUNT(DISTINCT hit.ip) DESC")
    List<StatisticDto> getStatisticUnique(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris,
                                          @Param("isEmpty") Boolean isEmpty);

    @Query("SELECT new ru.practicum.dto.StatisticDto(hit.app, hit.uri, COUNT(hit.ip)) " +
            "FROM Hit as hit " +
            "WHERE hit.timeStamp BETWEEN :start AND :end " +
            "AND (:isEmpty = true OR hit.uri IN (:uris)) " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER BY hitCount DESC")
    List<StatisticDto> getStatisticNotUnique(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             @Param("uris") List<String> uris,
                                             @Param("isEmpty") Boolean isEmpty);

}
