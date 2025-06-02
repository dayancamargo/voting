package com.assessment.voting.controller.agenda;

import com.assessment.voting.controller.BaseController;
import com.assessment.voting.dto.agenda.OpenSessionRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AgendaControllerTest extends BaseController {

    @BeforeEach
    public void cleanUp() {
        clearDatabase();
    }

    @Test
    void getAllAgendasSuccessfullyEmpty() {
        webTestClient.get().uri(url + "/v1/agenda")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody();
    }

    @Test
    void getAllAgendasSuccessfully() throws JsonProcessingException {
        String name = "Agenda 1";
        createAgenda(name);

        webTestClient.get().uri(url + "/v1/agenda")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo(name);
    }

    @Test
    void getAgendaByIdSuccessfully() throws JsonProcessingException {
        String name = "Agenda by id";
        var created = createAgenda(name);

        webTestClient.get().uri(url + "/v1/agenda/" + created)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo(name);
    }

    @Test
    void getAgendaByIdReturnsNotFound() {

        webTestClient.get().uri(url + "/v1/agenda/" + 111111111111111111L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody();
    }

    @Test
    void saveAgendaSuccessfully() {
        String name = "Agenda saved";
        webTestClient.post().uri(url + "/v1/agenda")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"" + name + "\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo(name)
                .jsonPath("$.startTime").doesNotExist()
                .jsonPath("$.endTime").doesNotExist();
    }

    @Test
    void openSessionSuccessfully() throws JsonProcessingException {
        String name = "Agenda to be opened";
        var created = createAgenda(name);

        OpenSessionRequest openSessionRequest = new OpenSessionRequest(created, "DAYS", 1);

        webTestClient.patch().uri(url + "/v1/agenda/open-session")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(openSessionRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.startTime").isNotEmpty()
                .jsonPath("$.endTime").isNotEmpty()
                .jsonPath("$.opened").isEqualTo(true);
    }
}