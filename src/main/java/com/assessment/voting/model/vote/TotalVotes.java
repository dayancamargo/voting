package com.assessment.voting.model.vote;

import java.util.Map;

public record TotalVotes(
    Long agendaId,
    String agendaName,
    Map<String, Integer> votes,
    Integer totalVotes
) {
}
