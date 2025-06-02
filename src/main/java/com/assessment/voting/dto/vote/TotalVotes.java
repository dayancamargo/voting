package com.assessment.voting.dto.vote;

import java.util.Map;

public record TotalVotes(
    Long agendaId,
    String agendaName,
    Map<String, Integer> votes,
    Integer totalVotes
) {
}
