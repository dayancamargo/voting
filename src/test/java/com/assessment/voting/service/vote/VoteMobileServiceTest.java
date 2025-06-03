package com.assessment.voting.service.vote;

import com.assessment.voting.dto.vote.TotalVotes;
import com.assessment.voting.dto.vote.VoteMobileRequest;
import com.assessment.voting.dto.vote.VoteRequest;
import com.assessment.voting.dto.vote.VoteResponse;
import com.assessment.voting.exception.CpfAlreadyVoted;
import com.assessment.voting.exception.CpfUnableToVote;
import com.assessment.voting.exception.NotFoundException;
import com.assessment.voting.model.enumType.InputTypeEnum;
import com.assessment.voting.model.enumType.ScreenType;
import com.assessment.voting.model.mobile.Item;
import com.assessment.voting.model.mobile.Screen;
import com.assessment.voting.service.screen.ScreenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteMobileServiceTest {
    @Mock
    private VoteService voteService;

    @Mock
    private ScreenService screenService;

    @Mock
    private ServerWebExchange exchange;

    @InjectMocks
    private VoteMobileService voteMobileService;

    @Test
    void voteSuccessfully() {
        VoteMobileRequest request = new VoteMobileRequest(1L, "SIM", "12345678901");
        Screen screen = getScreenFormTotal();
        TotalVotes total = new TotalVotes(1L, "Agenda 1", Map.of("SIM", 5, "NAO", 3), 8);

        when(voteService.vote(any())).thenReturn(Mono.just(new VoteResponse(1L, "12345678901", 1L)));
        when(voteService.countAllVotes(any())).thenReturn(Mono.just(total));
        when(screenService.getScreen(ScreenType.FORM_TOTAL)).thenReturn(Mono.just(screen));

        StepVerifier.create(voteMobileService.vote(exchange, request))
                .expectNextMatches(screenNext -> {
                    return screenNext.getItens().size() == 4 &&
                            screenNext.getItens().get(0).getTipo() == InputTypeEnum.TEXTO &&
                            screenNext.getItens().get(1).getId().equals("idNomeAgenda") && screenNext.getItens().get(1).getValor().equals("Agenda 1") &&
                            screenNext.getItens().get(2).getId().equals("idSim") && screenNext.getItens().get(2).getTexto().equals("5") &&
                            screenNext.getItens().get(3).getId().equals("idNao") && screenNext.getItens().get(3).getTexto().equals("3");
                })
                .verifyComplete();
    }

    @Test
    void voteFailsWhenAgendaNotFoundError() {
        VoteMobileRequest request = new VoteMobileRequest(1L, "SIM", "12345678901");
        VoteRequest vote = new VoteRequest(1L, "SIM", "12345678901");

        when(voteService.vote(vote)).thenReturn(Mono.error(new NotFoundException("Agenda not found with id: 1")));

        StepVerifier.create(voteMobileService.vote(exchange, request))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("Agenda not found with id: 1"))
                .verify();
    }

    @Test
    void voteFailsWhenCpfUnableToVoteError() {
        VoteMobileRequest request = new VoteMobileRequest(1L, "SIM", "12345678901");
        VoteRequest vote = new VoteRequest(1L, "SIM", "12345678901");

        when(voteService.vote(vote)).thenReturn(Mono.error(new CpfUnableToVote("12345678901")));

        StepVerifier.create(voteMobileService.vote(exchange, request))
                .expectErrorMatches(throwable -> throwable instanceof CpfUnableToVote &&
                        throwable.getMessage().equals("Cpf is unable to vote. CPF: 12345678901"))
                .verify();
    }

    @Test
    void voteFailsWhenCpfAlreadyVotedError() {
        VoteMobileRequest request = new VoteMobileRequest(1L, "SIM", "12345678901");
        VoteRequest vote = new VoteRequest(1L, "SIM", "12345678901");

        when(voteService.vote(vote)).thenReturn(Mono.error(new CpfAlreadyVoted("12345678901")));

        StepVerifier.create(voteMobileService.vote(exchange, request))
                .expectErrorMatches(throwable -> throwable instanceof CpfAlreadyVoted &&
                        throwable.getMessage().equals("Vote already exists in this agenda for this CPF: 12345678901"))
                .verify();
    }

    private Screen getScreenFormTotal() {
        Screen screen = new Screen();
        screen.setItens(List.of(
                Item.builder().tipo(InputTypeEnum.TEXTO).texto("").build(),
                Item.builder().id("idNomeAgenda").build(),
                Item.builder().id("idSim").texto("").build(),
                Item.builder().id("idNao").texto("").build()));
        screen.setBotaoCancelar(Item.builder().url("/").build());
        return screen;
    }
}