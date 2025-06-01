package com.assessment.voting.util;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

//@Component
public class DbInit {
//    @Bean(initMethod = "migrate")
//    @Profile("local")
    public Flyway flyway() {
        System.out.println("####### Using H2 in mem Flyway connection");
        return new Flyway(Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(
                        "jdbc:h2:file:~/testdb",
                        "",
                        "")
        );
    }
}
