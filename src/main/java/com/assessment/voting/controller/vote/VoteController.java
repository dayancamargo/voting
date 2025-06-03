package com.assessment.voting.controller.vote;

import com.assessment.voting.dto.vote.TotalVotes;
import com.assessment.voting.dto.vote.VoteRequest;
import com.assessment.voting.dto.vote.VoteResponse;
import com.assessment.voting.service.vote.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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