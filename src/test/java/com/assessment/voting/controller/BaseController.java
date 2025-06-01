package com.assessment.voting.controller;

import com.assessment.voting.model.SimNaoEnum;
import com.assessment.voting.model.agenda.AgendaEntity;
import com.assessment.voting.model.session.VoteEntity;
import com.assessment.voting.repository.AgendaRepository;
import com.assessment.voting.repository.VoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class BaseController {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    private AgendaRepository agendaRepository;
    @Autowired
    private VoteRepository voteRepository;


    protected String url = "http://localhost:8800";

    protected void clearDatabase() {
        voteRepository.deleteAll().block();
        agendaRepository.deleteAll().block();
    }

    protected Long createAgenda(String name) {
        var agendaEntity = AgendaEntity.builder().name(name).build();
        return agendaRepository.save(agendaEntity).block().getId();
    }

    protected void openOneMinuteSession(Long id, LocalDateTime startSession) {
        var endSession = startSession.plusMinutes(1);

        var agendaEntity = agendaRepository.findById(id).block();
        agendaEntity.setStartSession(startSession);
        agendaEntity.setEndSession(endSession);

        agendaRepository.save(agendaEntity).block();
    }

    protected void createVote(SimNaoEnum simNaoEnum, Long agenda, String cpf) {
        var voteEntity = VoteEntity.builder().answer(simNaoEnum).agendaId(agenda).cpf(cpf).build();
        voteRepository.save(voteEntity).block();
    }

}
