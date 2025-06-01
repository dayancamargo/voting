package com.assessment.voting.model.agenda;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("agenda")
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class AgendaEntity {
    @Id
    private Long id;
    @NotNull
    private String name;
    @Column("start_session")
    private LocalDateTime startSession;
    @Column("end_session")
    private LocalDateTime endSession;
}
