package com.assessment.voting.service;

import com.assessment.voting.exception.SessionCannotBeOpenedException;
import com.assessment.voting.model.agenda.OpenSessionRequest;
import com.assessment.voting.repository.AgendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

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
    void openSessionSuccessfully() {
        when(agendaRepository.isAbleToOpen(1L)).thenReturn(Mono.just(true));
        when(agendaRepository.openSession(any(), any(), any())).thenReturn(Mono.just(1));

        StepVerifier.create(agendaService.openSession(openSessionRequest))
                .expectNext("Session opened successfully for agenda with id: 1")
                .verifyComplete();
    }

    @Test
    void openSessionFailsWhenAgendaNotAbleToOpen() {
        when(agendaRepository.isAbleToOpen(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(agendaService.openSession(openSessionRequest))
                .expectErrorMatches(throwable -> throwable instanceof SessionCannotBeOpenedException &&
                        throwable.getMessage().equals("Not found a agenda able to open with id: 1"))
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