package com.assessment.voting.service;

import com.assessment.voting.exception.SessionCannotBeOpenedException;
import com.assessment.voting.model.TimeUnitEnum;
import com.assessment.voting.model.agenda.AgendaRequest;
import com.assessment.voting.model.agenda.AgendaEntity;
import com.assessment.voting.model.agenda.AgendaResponse;
import com.assessment.voting.model.agenda.OpenSessionRequest;
import com.assessment.voting.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public Flux<AgendaResponse> getAllAgendas() {
        log.info("Fetching all agendas");
        return agendaRepository.findAll().map(buildResponse());
    }

    public Mono<AgendaResponse> getAgendaById(Long id) {
        log.info("Fetching agenda with agendaId: {}", id);
        return agendaRepository.findById(id).map(buildResponse());
    }

    public Mono<AgendaResponse> save(AgendaRequest agendaRequest) {
        log.info("Saving agenda: {}", agendaRequest);

        var agendaEntity = AgendaEntity.builder()
                .name(agendaRequest.name())
                .build();

        return agendaRepository.save(agendaEntity).map(buildResponse());
    }

    @Transactional
    public Mono<AgendaResponse> openSession(OpenSessionRequest openSessionRequest) {
        log.info("Opening session for agenda with: {}", openSessionRequest);
        var startSession = LocalDateTime.now();
        var endSession = getEndSession(openSessionRequest, startSession);

        return agendaRepository.isAbleToOpen(openSessionRequest.agendaId())
                .filter(isAble -> isAble)
                .flatMap(isAble -> {
                    return agendaRepository.openSession(openSessionRequest.agendaId(), startSession, endSession)
                        .map(updatedRows -> {
                            if (updatedRows > 0) {
                                log.info("Session opened successfully for agenda with id: {}", openSessionRequest.agendaId());
                                return new AgendaResponse(
                                        openSessionRequest.agendaId(),
                                        null,
                                        startSession,
                                        endSession
                                );

                            }
                            log.error("Failed to open session for agenda with id: {}", openSessionRequest.agendaId());
                            throw new SessionCannotBeOpenedException("Failed to open session for agenda with id: " + openSessionRequest.agendaId());
                        });
                })
                .switchIfEmpty(Mono.error(new SessionCannotBeOpenedException("Could not find a agenda able to open with id: " + openSessionRequest.agendaId())));
    }

    private LocalDateTime getEndSession(OpenSessionRequest openSessionRequest, LocalDateTime startSession) {
        if( openSessionRequest.quantity() == null || openSessionRequest.timeUnit() == null) {
            log.warn("Quantity or time unit is null, defaulting to 1 minute");
            return startSession.plusMinutes(1);
        }

        return startSession.plus(openSessionRequest.quantity(), switch (TimeUnitEnum.fromString(openSessionRequest.timeUnit())) {
            case SECONDS -> ChronoUnit.SECONDS;
            case MINUTES -> ChronoUnit.MINUTES;
            case HOURS -> ChronoUnit.HOURS;
            case DAYS -> ChronoUnit.DAYS;
        });
    }

    private Function<AgendaEntity, AgendaResponse> buildResponse() {
        return savedEntity -> new AgendaResponse(
                savedEntity.getId(),
                savedEntity.getName(),
                savedEntity.getStartSession(),
                savedEntity.getEndSession()
        );
    }
}
