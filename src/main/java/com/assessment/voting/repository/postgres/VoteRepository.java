package com.assessment.voting.repository.postgres;

import com.assessment.voting.model.vote.VoteEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface VoteRepository extends R2dbcRepository<VoteEntity, Long> {

    Mono<Integer> countByAgendaIdAndAnswer(Long agendaId, String voteAnswer);
    Mono<Boolean> existsByAgendaIdAndCpf(Long agendaId, String cpf);
}
