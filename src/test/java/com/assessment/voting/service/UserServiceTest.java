package com.assessment.voting.service;

import com.assessment.voting.client.UserClient;
import com.assessment.voting.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    @Mock
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userClient);
    }

    @Test
    void shouldReturnTrueWhenCpfIsAbleToVote() {
        String cpf = "12345678900";
        when(userClient.validateCpf(cpf)).thenReturn(Mono.just("ABLE_TO_VOTE"));

        StepVerifier.create(userService.isCpfValid(cpf))
                .expectNext(true)
                .verifyComplete();

        verify(userClient, times(1)).validateCpf(cpf);
    }

    @Test
    void shouldReturnFalseWhenCpfIsNotAbleToVote() {
        String cpf = "12345678900";
        when(userClient.validateCpf(cpf)).thenReturn(Mono.just("UNABLE_TO_VOTE"));

        StepVerifier.create(userService.isCpfValid(cpf))
                .expectNext(false)
                .verifyComplete();

        verify(userClient, times(1)).validateCpf(cpf);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCpfIsNotFound() {
        String cpf = "12345678900";
        when(userClient.validateCpf(cpf)).thenReturn(Mono.empty());

        StepVerifier.create(userService.isCpfValid(cpf))
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException &&
                        throwable.getMessage().equals("User not found with CPF: " + cpf))
                .verify();

        verify(userClient, times(1)).validateCpf(cpf);
    }
}