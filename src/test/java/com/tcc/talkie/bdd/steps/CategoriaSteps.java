package com.tcc.talkie.bdd.steps;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import com.tcc.talkie.bdd.TestContext;
import com.tcc.talkie.repository.CategoryRepository;

import io.cucumber.java.pt.*;
public class CategoriaSteps {
    @Autowired
    private CategoryRepository categoriaRepository;

    @Autowired
    private TestContext context;


    @E("que o usuário seja um administrador")
    public void que_o_usuario_seja_um_administrador() {
        // O usuário criado no passo anterior já é um administrador, então não é necessário fazer nada aqui
    }

    @Então("o status da resposta deve ser {int}")
    public void o_status_da_resposta_deve_ser(int statusEsperado) throws Exception {
        context.getResultado().andExpect(MockMvcResultMatchers.status().is(statusEsperado));
    }
    
}
