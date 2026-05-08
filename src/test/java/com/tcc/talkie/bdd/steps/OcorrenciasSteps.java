package com.tcc.talkie.bdd.steps;

import io.cucumber.java.Before;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.tcc.talkie.bdd.TestContext;
import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.SubcategoryRepository;

import io.cucumber.java.pt.*;
import io.cucumber.spring.ScenarioScope;


@ScenarioScope
public class OcorrenciasSteps {
    @Autowired
    private CategoryRepository categoriaRepository;

    @Autowired
    private SubcategoryRepository subcategoriaRepository;

    @Autowired
    private TestContext context;

    @Before
    public void limparBanco(){
        categoriaRepository.deleteAll();
        subcategoriaRepository.deleteAll();
    }

    @E("que já exista categoria cadastrada")
    public void que_já_exista_categoria_cadastrada() {
       
        Category categoria = new Category();
        categoria.setName("Categoria Teste");
        categoria.setIcon("icon.png");
        categoria.setUser(context.getAuthenticatedUser());
        Category categoriaSalva = categoriaRepository.save(categoria);

        context.setCategoryId(categoriaSalva.getId());

        Subcategory subcategoria = new Subcategory();
        subcategoria.setName("Subcategoria Teste");
        subcategoria.setCategory(categoria);    
        Subcategory subcategoriaSalva = subcategoriaRepository.save(subcategoria);

        context.setSubcategoryId(subcategoriaSalva.getId());
    }

    @Então("o status da resposta deve ser sucesso")
    public void o_status_da_resposta_deve_ser_sucesso() throws Exception {
        context.getResultado().andExpect(MockMvcResultMatchers.status().is(200));
    }
}
