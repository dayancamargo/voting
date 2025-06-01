package com.assessment.voting.model.session;

import com.assessment.voting.model.SimNaoEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class VoteEntity {

    @Id
    private Long id;
    @Column("cpf")
    private String cpf;
    @Column("answer")
    private final SimNaoEnum answer;
    @Column("agenda_id")
    private final Long agendaId;
}
