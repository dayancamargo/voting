package com.assessment.voting.controller.agenda;

import com.assessment.voting.dto.agenda.AgendaMobileRequest;
import com.assessment.voting.dto.agenda.OpenSessionMobileRequest;
import com.assessment.voting.model.mobile.Screen;
import com.assessment.voting.service.agenda.AgendaMobileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "v2/agenda", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AgendaMobileController {

    private final AgendaMobileService agendaService;

    @PostMapping
    public Mono<Screen> getAgendas(ServerWebExchange exchange) {
        log.info("Get agendas");
        return agendaService.getAllAgendas(exchange);
    }

    @PostMapping("/criar")
    public Mono<Screen> createAgendas(ServerWebExchange exchange, @Valid @RequestBody AgendaMobileRequest agendaRequest) {
        return agendaService.create(exchange, agendaRequest);
    }

    @PostMapping("/{agendaId}")
    public Mono<Screen> getAgenda(ServerWebExchange exchange, @Valid @PathVariable("agendaId") Long agendaId) {
        log.info("Get agendas");
        return agendaService.getAgenda(exchange, agendaId);
    }

    @PostMapping("/abrir-sessao")
    public Mono<Screen> openSession(@Valid @RequestBody OpenSessionMobileRequest openSessionMobileRequest,
                                    ServerWebExchange exchange) {
        log.info("Opening session: {}", openSessionMobileRequest);
        return agendaService.openSession(exchange, openSessionMobileRequest);
    }
}