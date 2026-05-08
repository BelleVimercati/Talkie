package com.tcc.talkie.bdd.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.HashMap;
import java.util.Map;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.talkie.bdd.TestContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class GenericSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestContext context;

    @Quando("o usuário enviar uma requisição {string} para {string} com os seguintes dados:")
    public void enviarRequisicaoComBody(
            String metodo,
            String endpoint,
            DataTable dataTable
    ) throws Exception {

            Map<String, String> dados = new HashMap<>(
            dataTable.asMap(String.class, String.class)
        );

        dados.replaceAll((k, v) -> {

            if(v.equals("{categoryId}")) {
                return context.getCategoryId().toString();
            }

            if(v.equals("{subcategoryId}")) {
                return context.getSubcategoryId().toString();
            }

            return v;
        });

        String json = objectMapper.writeValueAsString(dados);

        switch (metodo.toUpperCase()) {

                case "POST":
                    MockHttpServletRequestBuilder postRequest = post(endpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json);

                    if (context.getTokenJWT() != null) {
                        postRequest.header("Authorization", "Bearer " + context.getTokenJWT());
                    }

                    context.setResultado(
                            mockMvc.perform(postRequest)
                                    .andDo(print())
                    );

                    break;

                case "PUT":
                    MockHttpServletRequestBuilder putRequest = put(endpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json);

                    if (context.getTokenJWT() != null) {
                        putRequest.header("Authorization", "Bearer " + context.getTokenJWT());
                    }

                    context.setResultado(
                            mockMvc.perform(putRequest)
                                    .andDo(print())
                    );

                    break;

                case "PATCH":
                    MockHttpServletRequestBuilder patchRequest = patch(endpoint)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json);

                    if (context.getTokenJWT() != null) {
                        patchRequest.header("Authorization", "Bearer " + context.getTokenJWT());
                    }

                    context.setResultado(
                            mockMvc.perform(patchRequest)
                                    .andDo(print())
                    );

                    break;

                default:
                    throw new IllegalArgumentException("Método HTTP inválido");
            }
    }
}
