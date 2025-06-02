package com.assessment.voting.service.agenda;

import com.assessment.voting.exception.SessionCannotBeOpenedException;
import com.assessment.voting.model.agenda.AgendaEntity;
import com.assessment.voting.dto.agenda.AgendaRequest;
import com.assessment.voting.dto.agenda.AgendaResponse;
import com.assessment.voting.dto.agenda.OpenSessionRequest;
import com.assessment.voting.repository.postgres.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaRequestServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private AgendaService agendaService;

    private final OpenSessionRequest openSessionRequest = new OpenSessionRequest(1L, "HOURS", 2);

    @Test
    void fetchAgendaByIdSuccessfully() {
        AgendaEntity agendaEntity = new AgendaEntity(1L, "Agenda 1", null, null);

        when(agendaRepository.findById(1L)).thenReturn(Mono.just(agendaEntity));

        StepVerifier.create(agendaService.getAgendaById(1L))
                .expectNext(new AgendaResponse(1L, "Agenda 1", null, null))
                .verifyComplete();
    }

    @Test
    void fetchAgendaByIdReturnsEmpty() {
        when(agendaRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(agendaService.getAgendaById(1L))
                .verifyComplete();
    }

    @Test
    void saveAgendaSuccessfully() {
        AgendaRequest agendaRequest = new AgendaRequest(null, "Agenda 1");
        AgendaEntity agendaEntity = new AgendaEntity(1L, "Agenda 1", null, null);

        when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(Mono.just(agendaEntity));

        StepVerifier.create(agendaService.save(agendaRequest))
                .expectNext(new AgendaResponse(1L, "Agenda 1", null, null))
                .verifyComplete();
    }

    @Test
    void openSessionSuccessfully() {
        when(agendaRepository.isAbleToOpen(1L)).thenReturn(Mono.just(true));
        when(agendaRepository.openSession(any(), any(), any())).thenReturn(Mono.just(1));

        StepVerifier.create(agendaService.openSession(openSessionRequest))
                .expectNextMatches(response ->
                        response.id().equals(1L) &&
                        response.startTime() != null &&
                        response.endTime() != null &&
                        response.startTime().isBefore(response.endTime()))
                .verifyComplete();
    }

    @Test
    void openSessionSuccessfullyWithoutInformedTime() {
        var openSessionRequest = new OpenSessionRequest(1L, null, null);

        when(agendaRepository.isAbleToOpen(1L)).thenReturn(Mono.just(true));
        when(agendaRepository.openSession(any(), any(), any())).thenReturn(Mono.just(1));

        StepVerifier.create(agendaService.openSession(openSessionRequest))
                .expectNextMatches(response ->
                        response.startTime().plusMinutes(1).equals(response.endTime()))
                .verifyComplete();
    }

    @Test
    void openSessionFailsWhenAgendaNotAbleToOpen() {
        when(agendaRepository.isAbleToOpen(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(agendaService.openSession(openSessionRequest))
                .expectErrorMatches(throwable -> throwable instanceof SessionCannotBeOpenedException &&
                        throwable.getMessage().equals("Could not find a agenda able to open with id: 1"))
                .verify();
    }

    @Test
    void openSessionFailsWhenUpdateFails() {
        when(agendaRepository.isAbleToOpen(1L)).thenReturn(Mono.just(true));
        when(agendaRepository.openSession(any(), any(), any())).thenReturn(Mono.just(0));

        StepVerifier.create(agendaService.openSession(openSessionRequest))
                .expectErrorMatches(throwable -> throwable instanceof SessionCannotBeOpenedException &&
                        throwable.getMessage().equals("Failed to open session for agenda with id: 1"))
                .verify();
    }
}