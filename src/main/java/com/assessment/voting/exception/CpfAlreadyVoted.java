package com.assessment.voting.exception;

public class CpfAlreadyVoted extends RuntimeException {
    public CpfAlreadyVoted(String cpf) {
        super("Vote already exists for this CPF: " + cpf);
    }
}
