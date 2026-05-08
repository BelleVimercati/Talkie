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


    @Então("o status da resposta deve ser {int}")
    public void o_status_da_resposta_deve_ser(int statusEsperado) throws Exception {
        context.getResultado().andExpect(MockMvcResultMatchers.status().is(statusEsperado));
    }
}
