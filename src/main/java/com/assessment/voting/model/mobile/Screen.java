package com.assessment.voting.model.mobile;

import com.assessment.voting.model.enumType.ScreenTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "mobile_screen")
public class Screen {

    @Id
    private String id;
    @Field
    private String titulo;
    @Field
    private ScreenTypeEnum tipo;
    @Field
    private List<Item> itens;
    @Field
    private Item botaoOk;
    @Field
    private Item botaoCancelar;
}
