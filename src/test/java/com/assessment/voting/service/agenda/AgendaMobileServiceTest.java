package com.assessment.voting.service.agenda;

import com.assessment.voting.dto.agenda.AgendaMobileRequest;
import com.assessment.voting.dto.agenda.AgendaRequest;
import com.assessment.voting.dto.agenda.AgendaResponse;
import com.assessment.voting.dto.agenda.OpenSessionMobileRequest;
import com.assessment.voting.dto.agenda.OpenSessionRequest;
import com.assessment.voting.model.enumType.ScreenType;
import com.assessment.voting.model.mobile.Item;
import com.assessment.voting.model.mobile.Screen;
import com.assessment.voting.service.agenda.AgendaService;
import com.assessment.voting.service.screen.ScreenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaMobileServiceTest {

    @Mock
    private AgendaService agendaService;

    @Mock
    private ScreenService screenService;

    @Mock
    private ServerWebExchange exchange;

    @InjectMocks
    private AgendaMobileService agendaMobileService;

    @Test
    void getAllAgendasSuccessfully() {
        Screen screen = new Screen();
        when(screenService.getScreen(ScreenType.SEL_PAUTA)).thenReturn(Mono.just(screen));
        when(agendaService.getAllAgendas()).thenReturn(Flux.just(new AgendaResponse(1L, "Agenda 1", null, null)));

        StepVerifier.create(agendaMobileService.getAllAgendas(exchange))
                .expectNextMatches(result -> result.getItens().size() == 1 &&
                        result.getItens().get(0).getTexto().equals("Agenda 1"))
                .verifyComplete();
    }

    @Test
    void getAgendaSuccessfullyWhenNotOpened() {
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", null, null);
        Screen screen = getScreenIniciaPauta();

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.just(agendaResponse));
        when(screenService.getScreen(ScreenType.FORM_INICIA_PAUTA)).thenReturn(Mono.just(screen));

        StepVerifier.create(agendaMobileService.getAgenda(exchange, 1L))
                .expectNextMatches(screenNext -> {
                    return screenNext.getItens().size() == 4 &&
                            screenNext.getItens().get(0).getId().equals("idAgenda") && screenNext.getItens().get(0).getValor().equals("1") &&
                            screenNext.getItens().get(1).getId().equals("idNomeAgenda") && screenNext.getItens().get(1).getValor().equals("Agenda 1") &&
                            screenNext.getItens().get(2).getId().equals("quantidadeDeTempo") &&
                            screenNext.getItens().get(3).getId().equals("unidadeTempo");
                })
                .verifyComplete();
    }

    @Test
    void getAgendaSuccessfullyWhenOpened() {
        LocalDateTime startime = LocalDateTime.now();
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", startime, startime.plusDays(1));
        Screen screen = getScreenFormVotar();

        when(agendaService.getAgendaById(1L)).thenReturn(Mono.just(agendaResponse));
        when(screenService.getScreen(ScreenType.FORM_VOTAR)).thenReturn(Mono.just(screen));

        StepVerifier.create(agendaMobileService.getAgenda(exchange, 1L))
                .expectNextMatches(screenNext -> {
                    return screenNext.getItens().size() == 1;
                })
                .verifyComplete();
    }

    @Test
    void openSessionSuccessfully() {
        OpenSessionMobileRequest request = new OpenSessionMobileRequest(1L, "HOURS", 2);
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", null, null);
        Screen screen = new Screen();
        when(agendaService.openSession(any(OpenSessionRequest.class))).thenReturn(Mono.just(agendaResponse));
        when(agendaService.getAllAgendas()).thenReturn(Flux.just(agendaResponse, agendaResponse));
        when(screenService.getScreen(ScreenType.SEL_PAUTA)).thenReturn(Mono.just(screen));

        StepVerifier.create(agendaMobileService.openSession(exchange, request))
                .expectNextMatches(screenNext -> {
                    return screenNext.getItens().size() == 2 &&
                            screenNext.getItens().get(0).getTexto().equals("Agenda 1") && screenNext.getItens().get(0).getBody().get("agendaId").equals("1");
                })
                .verifyComplete();
    }

    @Test
    void createAgendaSuccessfully() {
        AgendaMobileRequest agendaRequest = new AgendaMobileRequest("Agenda 1");
        AgendaResponse agendaResponse = new AgendaResponse(1L, "Agenda 1", null, null);
        Screen screen = getScreenIniciaPauta();
        when(agendaService.save(any(AgendaRequest.class))).thenReturn(Mono.just(agendaResponse));
        when(screenService.getScreen(ScreenType.FORM_INICIA_PAUTA)).thenReturn(Mono.just(screen));
        when(agendaService.getAgendaById(any())).thenReturn(Mono.just(agendaResponse));

        StepVerifier.create(agendaMobileService.create(exchange, agendaRequest))
                .expectNextMatches(screenNext -> {
                    return screenNext.getItens().size() == 4 &&
                            screenNext.getItens().get(0).getId().equals("idAgenda") && screenNext.getItens().get(0).getValor().equals("1") &&
                            screenNext.getItens().get(1).getId().equals("idNomeAgenda") && screenNext.getItens().get(1).getValor().equals("Agenda 1") &&
                            screenNext.getItens().get(2).getId().equals("quantidadeDeTempo") &&
                            screenNext.getItens().get(3).getId().equals("unidadeTempo");
                })
                .verifyComplete();
    }

    private Screen getScreenIniciaPauta() {
        Screen screen = new Screen();
        screen.setItens(List.of(
                Item.builder().id("idAgenda").build(),
                Item.builder().id("idNomeAgenda").build(),
                Item.builder().id("quantidadeDeTempo").build(),
                Item.builder().id("unidadeTempo").build()));
        screen.setBotaoOk(Item.builder().url("*").build());
        screen.setBotaoCancelar(Item.builder().url("/").build());
        return screen;
    }

    private Screen getScreenFormVotar() {
        Screen screen = new Screen();
        screen.setItens(List.of(
                Item.builder().id("idAgenda").build()));
        screen.setBotaoCancelar(Item.builder().url("/").build());
        return screen;
    }
}