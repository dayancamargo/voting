package com.assessment.voting.exception;

public class CpfAlreadyVoted extends BusinessException {
    public CpfAlreadyVoted(String cpf) {
        super("Vote already exists in this agenda for this CPF: " + cpf);
    }
}
