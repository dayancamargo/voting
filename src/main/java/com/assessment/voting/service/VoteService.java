package com.assessment.voting.service;

import com.assessment.voting.exception.CpfUnableToVote;
import com.assessment.voting.exception.NotFoundException;
import com.assessment.voting.exception.SessionNotOpenedException;
import com.assessment.voting.exception.CpfAlreadyVoted;
import com.assessment.voting.model.SimNaoEnum;
import com.assessment.voting.model.agenda.AgendaResponse;
import com.assessment.voting.model.session.VoteEntity;
import com.assessment.voting.model.vote.TotalVotes;
import com.assessment.voting.model.vote.VoteRequest;
import com.assessment.voting.model.vote.VoteResponse;
import com.assessment.voting.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {
    private final VoteRepository voteRepository;
    private final AgendaService agendaService;
    private final UserService userService;

    public Mono<VoteResponse> vote(VoteRequest voteRequest) {
        log.info("Processing vote");

        return agendaService.getAgendaById(voteRequest.agenda())
                .switchIfEmpty(Mono.error(new NotFoundException("Agenda not found")))
                .flatMap(agenda -> {
                    log.info("Agenda found: {}", agenda);
                    if (!agenda.isOpened()) {
                        log.warn("Agenda is not opened, cannot process vote");
                        return Mono.error(new SessionNotOpenedException("Agenda is not opened"));
                    }
                    log.info("Agenda is opened, proceeding with vote");
                    return validateCpf(voteRequest, agenda)
                            .flatMap(exists -> {
                                log.info("CPF has not voted yet, proceeding with saving vote");
                                var voteEntity = buildEntity(voteRequest);
                                return saveVote(voteEntity);
                            });
                });
    }

    public Mono<TotalVotes> countAllVotes(Long agendaId) {
        log.info("Counting all votes for agenda with id: {}", agendaId);

        return agendaService.getAgendaById(agendaId)
                .switchIfEmpty(Mono.error(new NotFoundException("Agenda not found with id: " + agendaId)))
                .flatMap(agenda -> Mono.zip(
                        voteRepository.countByAgendaIdAndAnswer(agendaId, SimNaoEnum.SIM.name()),
                        voteRepository.countByAgendaIdAndAnswer(agendaId, SimNaoEnum.NAO.name())
                ).map(tuple -> {
                    var simCount = tuple.getT1();
                    var naoCount = tuple.getT2();
                    log.info("Agenda: {}, SIM votes: {}, NAO votes: {}", agenda, simCount, naoCount);

                    return new TotalVotes(agenda.id(),
                                          agenda.name(),
                                          Map.of(SimNaoEnum.SIM.name(), simCount, SimNaoEnum.NAO.name(), naoCount),
                                          (simCount + naoCount));
                }));
    }

    private Mono<Boolean> validateCpf(VoteRequest voteRequest, AgendaResponse agenda) {
        return Mono.zip(
                isAbleToVote(voteRequest.cpf()),
                alreadyVoted(voteRequest, agenda)
        ).flatMap(tuple -> {
            boolean isCpfValid = tuple.getT1();
            boolean alreadyVoted = tuple.getT2();

            log.info("CPF validation result: isCpfValid={}, alreadyVoted={}", isCpfValid, alreadyVoted);

            if (!isCpfValid) {
                return Mono.error(new CpfUnableToVote(voteRequest.cpf()));
            }

            if (alreadyVoted) {
                return Mono.error(new CpfAlreadyVoted(voteRequest.cpf()));
            }

            return Mono.just(true);
        });
    }

    private Mono<Boolean> alreadyVoted(VoteRequest voteRequest, AgendaResponse agenda) {
        return voteRepository.existsByAgendaIdAndCpf(agenda.id(), voteRequest.cpf());
    }

    private Mono<Boolean> isAbleToVote(String cpf) {
        return userService.isCpfValid(cpf);
    }

    private Mono<VoteResponse> saveVote(VoteEntity voteEntity) {
        return voteRepository.save(voteEntity)
                .map(savedVote -> new VoteResponse(savedVote.getId(), savedVote.getCpf(), savedVote.getAgendaId()))
                .doOnSuccess(savedVote -> log.info("Vote saved successfully: {}", savedVote));
    }

    private VoteEntity buildEntity(VoteRequest voteRequest) {
        return VoteEntity.builder()
                .agendaId(voteRequest.agenda())
                .cpf(voteRequest.cpf())
                .answer(SimNaoEnum.fromString(voteRequest.answer()))
                .build();
    }
}
