package com.assessment.voting.exception;

public class CpfUnableToVote extends BusinessException {
    public CpfUnableToVote(String cpf) {
        super("Cpf is unable to vote. CPF: " + cpf);
    }
}
