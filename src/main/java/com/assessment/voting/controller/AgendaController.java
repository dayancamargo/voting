package com.assessment.voting.controller;

import com.assessment.voting.exception.NotFoundException;
import com.assessment.voting.model.agenda.AgendaRequest;
import com.assessment.voting.model.agenda.AgendaResponse;
import com.assessment.voting.model.agenda.OpenSessionRequest;
import com.assessment.voting.service.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "v1/agenda", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class AgendaController {

    private final AgendaService agendaService;

    @GetMapping
    public Flux<AgendaResponse> getAllAgendas() {
        log.info("Fetching all Agendas");
        return agendaService.getAllAgendas();
    }

    @GetMapping("/{agendaId}")
    public Mono<AgendaResponse> getById(@PathVariable("agendaId") Long id) {
        log.info("Fetching agenda with agendaId: {}", id);
        return agendaService.getAgendaById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Agenda not found with agendaId: " + id)));
    }

    @PostMapping
    public Mono<AgendaResponse> save(@Valid @RequestBody AgendaRequest agendaRequest) {
        log.info("Creating Agenda: {}", agendaRequest);
        return agendaService.save(agendaRequest);
    }

    @PatchMapping("/open-session")
    public Mono<String> openSession(@Valid @RequestBody OpenSessionRequest openSessionRequest) {
        log.info("Opening session: {}", openSessionRequest);
        return agendaService.openSession(openSessionRequest);
    }
}