package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(Long requesterId);

    @Query("select count(pr) from ParticipationRequest pr " +
            "where (pr.event.id = :id) " +
            "and (pr.status = 'CONFIRMED') ")
    Integer getConfirmedRequestsByEventId(@Param("id") Long id);

    @Query("select pr from ParticipationRequest pr " +
            "where (pr.event.id = :eventId) " +
            "and (pr.requester.id = :userId) ")
    ParticipationRequest getRequestByUserIdAndEventId(@Param("userId") Long userId,
                                                      @Param("eventId") Long eventId);

    @Query("select pr from ParticipationRequest pr " +
            "where (pr.id in :requestIds) ")
    List<ParticipationRequest> getRequestsByRequestIds(@Param("requestIds") List<Long> requestIds);
}
