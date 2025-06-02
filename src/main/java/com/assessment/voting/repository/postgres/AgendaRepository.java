package com.assessment.voting.repository.postgres;

import com.assessment.voting.model.agenda.AgendaEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface AgendaRepository extends R2dbcRepository<AgendaEntity, Long> {

    @Modifying
    @Query("UPDATE agenda " +
            "SET end_Session = :endSession, start_Session= :startSession " +
            "WHERE id = :id AND start_Session IS NULL")
    Mono<Integer> openSession(Long id, LocalDateTime startSession, LocalDateTime endSession);

    @Query(" SELECT count(1)>0  " +
           " FROM agenda a WHERE a.id = :id AND a.start_Session IS NULL")
    Mono<Boolean> isAbleToOpen(@Param("id") Long id);
}
