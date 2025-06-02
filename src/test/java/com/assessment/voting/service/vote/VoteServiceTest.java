package com.assessment.voting.service.vote;

import com.assessment.voting.exception.CpfAlreadyVoted;
import com.assessment.voting.exception.CpfUnableToVote;
import com.assessment.voting.exception.NotFoundException;
import com.assessment.voting.exception.SessionNotOpenedException;
import com.assessment.voting.model.enumType.SimNaoEnum;
import com.assessment.voting.dto.agenda.AgendaResponse;
import com.assessment.voting.model.vote.VoteEntity;
import com.assessment.voting.dto.agenda.TotalVotes;
import com.assessment.voting.dto.vote.VoteRequest;
import com.assessment.voting.dto.vote.VoteResponse;
import com.assessment.voting.repository.postgres.VoteRepository;
import com.assessment.voting.service.agenda.AgendaService;
import com.assessment.voting.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private AgendaService agendaService;

    @Mock
    private UserService userService;

    @InjectMocks
    private VoteService voteService;

    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endime = LocalDateTime.now().plusDays(1);

    @Test
    void voteSuccessfully() {
        VoteRequest voteRequest = new VoteRequest(1L, "SIM", "12345678901");
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", startTime, endime);

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.just(agendaResponse));
        when(userService.isCpfValid("12345678901")).thenReturn(Mono.just(true));
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(Mono.just(false));
        when(voteRepository.save(any(VoteEntity.class))).thenReturn(Mono.just(new VoteEntity(1L, "12345678901", SimNaoEnum.SIM, 1L)));

        StepVerifier.create(voteService.vote(voteRequest))
                .expectNext(new VoteResponse(1L, "12345678901", 1L))
                .verifyComplete();
    }

    @Test
    void voteFailsWhenAgendaNotFound() {
        VoteRequest voteRequest = new VoteRequest(1L, "SIM", "12345678901");

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(voteService.vote(voteRequest))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Agenda not found"))
                .verify();
    }

    @Test
    void voteFailsWhenAgendaNotOpened() {
        VoteRequest voteRequest = new VoteRequest(1L, "SIM", "12345678901");
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", null, null);

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.just(agendaResponse));

        StepVerifier.create(voteService.vote(voteRequest))
                .expectErrorMatches(throwable -> throwable instanceof SessionNotOpenedException &&
                        throwable.getMessage().equals("Agenda is not opened"))
                .verify();
    }

    @Test
    void voteFailsWhenAgendaExpired() {
        VoteRequest voteRequest = new VoteRequest(1L, "SIM", "12345678901");
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", startTime.minusDays(2), startTime.minusDays(1));

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.just(agendaResponse));

        StepVerifier.create(voteService.vote(voteRequest))
                .expectErrorMatches(throwable -> throwable instanceof SessionNotOpenedException &&
                        throwable.getMessage().equals("Agenda is not opened"))
                .verify();
    }

    @Test
    void voteFailsWhenCpfUnableToVote() {
        VoteRequest voteRequest = new VoteRequest(1L, "SIM", "12345678901");
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", startTime, endime);

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.just(agendaResponse));
        when(userService.isCpfValid("12345678901")).thenReturn(Mono.just(false));
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(Mono.just(false));

        StepVerifier.create(voteService.vote(voteRequest))
                .expectErrorMatches(throwable -> throwable instanceof CpfUnableToVote &&
                        throwable.getMessage().equals("Cpf is unable to vote. CPF: 12345678901"))
                .verify();
    }

    @Test
    void voteFailsWhenCpfAlreadyVoted() {
        VoteRequest voteRequest = new VoteRequest(1L, "SIM", "12345678901");
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", startTime, endime);

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.just(agendaResponse));
        when(userService.isCpfValid("12345678901")).thenReturn(Mono.just(true));
        when(voteRepository.existsByAgendaIdAndCpf(1L, "12345678901")).thenReturn(Mono.just(true));

        StepVerifier.create(voteService.vote(voteRequest))
                .expectErrorMatches(throwable -> throwable instanceof CpfAlreadyVoted &&
                        throwable.getMessage().equals("Vote already exists in this agenda for this CPF: 12345678901"))
                .verify();
    }

    @Test
    void countAllVotesSuccessfully() {
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", startTime, endime);

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.just(agendaResponse));
        when(voteRepository.countByAgendaIdAndAnswer(1L, SimNaoEnum.SIM.name())).thenReturn(Mono.just(5));
        when(voteRepository.countByAgendaIdAndAnswer(1L, SimNaoEnum.NAO.name())).thenReturn(Mono.just(3));

        StepVerifier.create(voteService.countAllVotes(1L))
                .expectNext(new TotalVotes(1L, "Agenda 1", Map.of("SIM", 5, "NAO", 3), 8))
                .verifyComplete();
    }

    @Test
    void countAllVotesFailsWhenAgendaNotFound() {
        when(agendaService.getAgendaById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(voteService.countAllVotes(1L))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Agenda not found with id: 1"))
                .verify();
    }
}