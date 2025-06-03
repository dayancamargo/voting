package com.assessment.voting.service.agenda;

import com.assessment.voting.dto.agenda.AgendaMobileRequest;
import com.assessment.voting.dto.agenda.AgendaRequest;
import com.assessment.voting.dto.agenda.AgendaResponse;
import com.assessment.voting.dto.agenda.OpenSessionMobileRequest;
import com.assessment.voting.dto.agenda.OpenSessionRequest;
import com.assessment.voting.model.enumType.ScreenType;
import com.assessment.voting.model.mobile.Item;
import com.assessment.voting.model.mobile.Screen;
import com.assessment.voting.service.screen.ScreenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgendaMobileService {
    private final AgendaService agendaService;
    private final ScreenService screenService;

    public Mono<Screen> getAllAgendas(ServerWebExchange exchange) {
        log.info("Fetching all Agendas for mobile");
        return screenService.getScreen(ScreenType.SEL_PAUTA)
                .zipWith(agendaService.getAllAgendas().collectList())
                .map(
                    tuple -> {
                        var screenEntity = tuple.getT1();
                        var agendas = tuple.getT2();
                        var items = agendas.stream()
                                .map(agendasItem -> Item.builder()
                                        .texto(agendasItem.name())
                                        .url(screenService.generateActionUrl(exchange, "/v2/agenda/" + agendasItem.id()))
                                        .body(new HashMap<>() {{
                                            put("agendaId", agendasItem.id().toString());
                                        }})
                                        .build())
                                .toList();
                        screenEntity.setItens(items);
                        return screenEntity;
                    }
                );
    }

    public Mono<Screen> getAgenda(ServerWebExchange exchange, Long agendaId) {
        log.info("Get Agenda {} for mobile", agendaId);
        return agendaService.getAgendaById(agendaId)
                .flatMap( agenda -> {
                    log.info("Agenda found: {}", agenda);
                    if(agenda.isOpened())
                        return getFormVote(exchange, agenda);
                    else
                        return getFormStartSession(exchange, agenda);
                });
    }

    private Mono<Screen> getFormStartSession(ServerWebExchange exchange, AgendaResponse agenda) {
        return screenService.getScreen(ScreenType.FORM_INICIA_PAUTA)
                .map(screenEntity -> {
                    log.info("Get start session form agenda {}", agenda.id());
                    if(screenEntity.getItens() != null) {
                        screenEntity.getItens().stream()
                                .filter(item -> item.getId() != null)
                                .forEach(item -> {
                                    switch (item.getId()) {
                                        case "idAgenda" -> item.setValor(agenda.id().toString());
                                        case "idNomeAgenda" -> item.setValor(agenda.name());
                                        case "quantidadeDeTempo" -> item.setValor("");
                                        case "unidadeTempo" -> item.setValor("");
                                    }
                                });
                    }
                    if(screenEntity.getBotaoOk() != null)
                        screenEntity.getBotaoOk().setUrl(screenService.generateActionUrl(exchange, screenEntity.getBotaoOk().getUrl()));
                    if(screenEntity.getBotaoCancelar() != null)
                        screenEntity.getBotaoCancelar().setUrl(screenService.generateActionUrl(exchange, screenEntity.getBotaoCancelar().getUrl()));

                    return screenEntity;
                });
    }

    private Mono<Screen> getFormVote(ServerWebExchange exchange, AgendaResponse agenda) {
        return screenService.getScreen(ScreenType.FORM_VOTAR)
                .map(screenEntity -> {
                    log.info("Get voting form agenda {}", agenda.id());
                    screenEntity.getItens().stream()
                            .filter(item -> item.getId() != null)
                            .forEach(item -> {
                                if (item.getId().equals("idAgenda")) {
                                    item.setValor(agenda.id().toString());
                                }
                            });
                    if(screenEntity.getBotaoOk() != null)
                        screenEntity.getBotaoOk().setUrl(screenService.generateActionUrl(exchange, screenEntity.getBotaoOk().getUrl()));
                    if(screenEntity.getBotaoCancelar() != null)
                        screenEntity.getBotaoCancelar().setUrl(screenService.generateActionUrl(exchange, screenEntity.getBotaoCancelar().getUrl()));

                    return screenEntity;
                });
    }

    public Mono<Screen> openSession(ServerWebExchange exchange, OpenSessionMobileRequest request) {
        log.info("Open session {}", request);

        OpenSessionRequest openSessionRequest = new OpenSessionRequest(
                request.idAgenda(),
                request.unidadeTempo(),
                request.quantidadeDeTempo()
                );

        return agendaService.openSession(openSessionRequest)
                .flatMap( agendaResponse -> {
                    log.info("Session opened for agenda {}, returning to selection page", agendaResponse.id());
                    return getAllAgendas(exchange);
                });
    }

    public Mono<Screen> create(ServerWebExchange exchange, AgendaMobileRequest agendaRequest) {
        log.info("Creating agenda {}", agendaRequest);

        return agendaService.save(new AgendaRequest(null, agendaRequest.nome()))
                .flatMap(agenda -> {
                    log.info("Agenda created with id: {}", agenda.id());
                    return getAgenda(exchange, agenda.id());
                });
    }
}
