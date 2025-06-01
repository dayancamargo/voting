package com.assessment.voting.controller;

import com.assessment.voting.client.UserClient;
import com.assessment.voting.model.SimNaoEnum;
import com.assessment.voting.model.vote.VoteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VoteControllerTest extends BaseController {
    @MockitoBean
    private UserClient userClient;

    @BeforeEach
    public void cleanUp() {
        clearDatabase();
    }

    @Test
    void getVotesFromAgendaSuccessfully() {
        var agenda = createAgenda("Agenda 1");
        openOneMinuteSession(agenda, LocalDateTime.now());
        createVote(SimNaoEnum.SIM, agenda, "12345678901");
        createVote(SimNaoEnum.SIM, agenda, "12345678902");
        createVote(SimNaoEnum.NAO, agenda, "12345678903");

        webTestClient.get().uri(url + "/v1/vote/agenda/" + agenda)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.agendaId").isEqualTo(agenda)
                .jsonPath("$.agendaName").isEqualTo("Agenda 1")
                .jsonPath("$.totalVotes").isEqualTo(3)
        ;
    }

    @Test
    void getVotesFromAgendaNotFoundAgenda() {
        webTestClient.get().uri(url + "/v1/vote/agenda/1284791284971")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody();
    }

    @Test
    void voteSuccessfully() {
        Mockito.when(userClient.validateCpf("12345678901"))
                .thenReturn(Mono.just("ABLE_TO_VOTE"));

        var agenda = createAgenda("Agenda 1");
        openOneMinuteSession(agenda, LocalDateTime.now());
        VoteRequest voteRequest = new VoteRequest(agenda, SimNaoEnum.SIM.name(), "12345678901");

        webTestClient.post().uri(url + "/v1/vote")
                .bodyValue(voteRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.agenda").isEqualTo(agenda)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.cpf").isEqualTo("12345678901");
    }

    @Test
    void voteFailWithoutAgenda() {
        VoteRequest voteRequest = new VoteRequest(1L, SimNaoEnum.SIM.name(), "12345678901");

        webTestClient.post().uri(url + "/v1/vote")
                .bodyValue(voteRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody();
    }

    @Test
    void voteFailWithoutOpenedAgenda() {
        var agenda = createAgenda("Agenda");
        VoteRequest voteRequest = new VoteRequest(agenda, SimNaoEnum.SIM.name(), "12345678901");

        webTestClient.post().uri(url + "/v1/vote")
                .bodyValue(voteRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Agenda is not opened");
    }

    @Test
    void voteFailCpfAlreadyVoted() {
        var agenda = createAgenda("Agenda");
        VoteRequest voteRequest = new VoteRequest(agenda, SimNaoEnum.SIM.name(), "12345678901");
        createVote(SimNaoEnum.SIM, agenda, "12345678901");
        openOneMinuteSession(agenda, LocalDateTime.now());
        Mockito.when(userClient.validateCpf("12345678901"))
                .thenReturn(Mono.just("ABLE_TO_VOTE"));

        webTestClient.post().uri(url + "/v1/vote")
                .bodyValue(voteRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Vote already exists in this agenda for this CPF: 12345678901");
    }

    @Test
    void voteFailCpfUnableVoted() {
        var agenda = createAgenda("Agenda");
        VoteRequest voteRequest = new VoteRequest(agenda, SimNaoEnum.SIM.name(), "12345678902");
        openOneMinuteSession(agenda, LocalDateTime.now());

        Mockito.when(userClient.validateCpf("12345678902"))
                .thenReturn(Mono.just("UNABLE_TO_VOTE"));

        webTestClient.post().uri(url + "/v1/vote")
                .bodyValue(voteRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Cpf is unable to vote. CPF: 12345678902");
    }
}