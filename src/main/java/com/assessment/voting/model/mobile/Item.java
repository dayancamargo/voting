package com.assessment.voting.model.mobile;

import com.assessment.voting.model.enumType.InputTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class Item {
    @Field(value = "id")
    private String id;
    @Field
    private InputTypeEnum tipo;
    @Field
    private String titulo;
    @Field
    private String valor;
    @Field
    private String texto;
    @Field
    private String url;
    @Field
    private HashMap<String, String> body;
}
