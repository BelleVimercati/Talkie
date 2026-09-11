package com.tcc.talkie.bdd.steps;

import io.cucumber.java.Before;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.tcc.talkie.bdd.TestContext;
import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.OccurrenceRepository;
import com.tcc.talkie.repository.SubcategoryRepository;
import com.tcc.talkie.repository.UserRepository;

import io.cucumber.java.pt.*;
import io.cucumber.spring.ScenarioScope;


@ScenarioScope
public class OcorrenciasSteps {
    @Autowired
    private CategoryRepository categoriaRepository;

    @Autowired
    private SubcategoryRepository subcategoriaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestContext context;

    @Autowired
    private OccurrenceRepository occurrenceRepository;


    @E("que já exista categoria cadastrada")
    public void que_já_exista_categoria_cadastrada() {

        //primeiro: criar um usuário para categoria
        User user = new User();
        user.setName("Usuário Teste");
        user.setEmail("usuario" + System.currentTimeMillis() + "@teste.com");
        user.setCpf(UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 11));
        user.setPassword("123456");
        user.setRole(Role.ADMIN);

        User usuarioSalvo = userRepository.save(user);
       
        Category categoria = new Category();
        categoria.setName("Categoria Teste" + UUID.randomUUID());
        categoria.setIcon("icon.png");
        categoria.setUser(usuarioSalvo);
        Category categoriaSalva = categoriaRepository.save(categoria);

        context.setCategoryId(categoriaSalva.getId());

        Subcategory subcategoria = new Subcategory();
        subcategoria.setName("Subcategoria Teste" + UUID.randomUUID());
        subcategoria.setCategory(categoriaSalva);    
        Subcategory subcategoriaSalva = subcategoriaRepository.save(subcategoria);

        context.setSubcategoryId(subcategoriaSalva.getId());

        System.out.println(context.getCategoryId());
        System.out.println(context.getSubcategoryId());
    }

    @Então("o status da resposta deve ser sucesso")
    public void o_status_da_resposta_deve_ser_sucesso() throws Exception {
        context.getResultado().andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Então("o status da resposta deve ser não autorizado")
    public void o_status_da_resposta_deve_ser_não_autorizado() throws Exception {
        context.getResultado().andExpect(MockMvcResultMatchers.status().is(401));
    }
}
