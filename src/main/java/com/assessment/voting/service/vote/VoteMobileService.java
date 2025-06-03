package com.assessment.voting.service.vote;

import com.assessment.voting.dto.vote.VoteMobileRequest;
import com.assessment.voting.dto.vote.VoteRequest;
import com.assessment.voting.model.enumType.ScreenType;
import com.assessment.voting.model.enumType.SimNaoEnum;
import com.assessment.voting.model.mobile.Screen;
import com.assessment.voting.service.screen.ScreenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteMobileService {
    private final VoteService voteService;
    private final ScreenService screenService;

    public Mono<Screen> vote(ServerWebExchange exchange, VoteMobileRequest request) {

        VoteRequest vote = new VoteRequest(
                request.idAgenda(),
                request.idVoto(),
                request.idCpf());

        log.info("Voting Agenda: {}", request);
        return voteService.vote(vote)
                .flatMap(
                    response -> screenService.getScreen(ScreenType.FORM_TOTAL)
                            .zipWith(voteService.countAllVotes(vote.agenda()))
                        .map(tuple -> {
                            var screen = tuple.getT1();
                            var countVotes = tuple.getT2();
                            screen.getItens().stream()
                                    .filter(item -> item.getId() != null)
                                    .forEach(item -> {
                                        switch (item.getId()) {
                                            case "idNomeAgenda" -> item.setValor(countVotes.agendaName());
                                            case "idSim" -> item.setTexto(item.getTexto().concat(countVotes.votes().getOrDefault(SimNaoEnum.SIM.name(), 0).toString()));
                                            case "idNao" -> item.setTexto(item.getTexto().concat(countVotes.votes().getOrDefault(SimNaoEnum.NAO.name(), 0).toString()));
                                        }
                                    });
                            if(screen.getBotaoCancelar() !=null)
                                screen.getBotaoCancelar().setUrl(screenService.generateActionUrl(exchange, screen.getBotaoCancelar().getUrl()));
                            return screen;
                        }))
                .doOnError(error -> log.error("Error while voting: {}", error.getMessage()));
    }
}
