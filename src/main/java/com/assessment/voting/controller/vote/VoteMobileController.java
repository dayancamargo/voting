package com.assessment.voting.controller.vote;

import com.assessment.voting.model.mobile.Screen;
import com.assessment.voting.dto.vote.VoteMobileRequest;
import com.assessment.voting.service.vote.VoteMobileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "v2/vote", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class VoteMobileController {

    private final VoteMobileService voteService;

    @PostMapping("/votar")
    public Mono<Screen> save(@Valid @RequestBody VoteMobileRequest voteRequest, ServerWebExchange exchange) {
        log.info("Voting Agenda: {}", voteRequest);
        return voteService.vote(exchange, voteRequest);
    }
}