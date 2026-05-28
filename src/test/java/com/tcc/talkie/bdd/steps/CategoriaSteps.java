package com.tcc.talkie.bdd.steps;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.tcc.talkie.bdd.TestContext;
import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.repository.CategoryRepository;

import io.cucumber.java.pt.*;
public class CategoriaSteps {
    @Autowired
    private TestContext context;

    @Autowired
    private CategoryRepository categoryRepository;

    @E("que já exista uma categoria cadastrada com nome {string}")
    public void que_ja_exista_uma_categoria_cadastrada_com_nome(String nome) {
        Category category = new Category();
        category.setName(nome);
        category.setIcon("teste");
        category.setUser(context.getAuthenticatedUser());

        categoryRepository.save(category);
    }


    @Então("o status da resposta deve ser {int}")
    public void o_status_da_resposta_deve_ser(int statusEsperado) throws Exception {
        context.getResultado().andExpect(MockMvcResultMatchers.status().is(statusEsperado));
    }
}
