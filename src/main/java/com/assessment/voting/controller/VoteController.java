package com.assessment.voting.controller;

import com.assessment.voting.model.vote.TotalVotes;
import com.assessment.voting.model.vote.VoteRequest;
import com.assessment.voting.model.vote.VoteResponse;
import com.assessment.voting.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "v1/vote")
@RequiredArgsConstructor
@Slf4j
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public Mono<VoteResponse> save(@Valid @RequestBody VoteRequest voteRequest) {
        log.info("Voting Agenda: {}", voteRequest);
        return voteService.vote(voteRequest);
    }

    @GetMapping("/agenda/{agendaId}")
    public Mono<TotalVotes> getVotesFromAgenda(@PathVariable("agendaId") Long agendaId) {
        log.info("Counting votes for agenda with id: {}", agendaId);
        return voteService.countAllVotes(agendaId);
    }
}