package com.assessment.voting.service;

import com.assessment.voting.client.UserClient;
import com.assessment.voting.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserClient userClient;

    public Mono<Boolean> isCpfValid(String cpf) {
        log.info("Validating CPF: {}", cpf);
        return userClient.validateCpf(cpf)
                .flatMap(result -> {
                    if ("ABLE_TO_VOTE".equals(result)) {
                        return Mono.just(Boolean.TRUE);
                    }
                    return Mono.just(Boolean.FALSE);
                })
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with CPF: " + cpf)));
    }
}
