package com.assessment.voting.service.screen;

import com.assessment.voting.exception.NotFoundException;
import com.assessment.voting.model.enumType.ScreenType;
import com.assessment.voting.model.mobile.Screen;
import com.assessment.voting.repository.mongo.ScreenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenService {
    private final ScreenRepository screenRepository;

    public Mono<Screen> getScreen(ScreenType screen) {
        log.info("Fetching screen with ID: {}", screen);
        return screenRepository.findById(screen.name())
                .switchIfEmpty(Mono.error(new NotFoundException("Screen not found with ID: " + screen)));
    }

    public String generateActionUrl(ServerWebExchange exchange, String action) {
        var uri = exchange.getRequest().getURI();
        return uri.toString().replace(uri.getPath(), "").concat(action);
    }
}
