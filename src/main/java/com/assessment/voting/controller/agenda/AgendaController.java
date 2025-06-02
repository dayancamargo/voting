package com.assessment.voting.controller.agenda;

import com.assessment.voting.exception.NotFoundException;
import com.assessment.voting.dto.agenda.AgendaRequest;
import com.assessment.voting.dto.agenda.AgendaResponse;
import com.assessment.voting.dto.agenda.OpenSessionRequest;
import com.assessment.voting.service.agenda.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AgendaResponse> save(@Valid @RequestBody AgendaRequest agendaRequest) {
        log.info("Creating Agenda: {}", agendaRequest);
        return agendaService.save(agendaRequest);
    }

    @PatchMapping("/open-session")
    public Mono<AgendaResponse> openSession(@Valid @RequestBody OpenSessionRequest openSessionRequest) {
        log.info("Opening session: {}", openSessionRequest);
        return agendaService.openSession(openSessionRequest);
    }
}