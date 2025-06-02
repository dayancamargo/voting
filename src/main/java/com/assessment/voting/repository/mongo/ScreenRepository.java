package com.assessment.voting.repository.mongo;

import com.assessment.voting.model.mobile.Screen;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ScreenRepository extends ReactiveCrudRepository<Screen, String> {
}
