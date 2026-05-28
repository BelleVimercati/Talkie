package com.tcc.talkie.bdd;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import com.tcc.talkie.domain.user.User;

import io.cucumber.spring.ScenarioScope;

@Component
@ScenarioScope
public class TestContext {
    
    private User authenticatedUser;

    private String tokenJwt;

    private Long categoryId;

    private Long subcategoryId;

    private ResultActions resultado;

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public String getTokenJWT(){
        return tokenJwt;
    }

    public void setTokenJWT(String tokenJwt){
        this.tokenJwt = tokenJwt;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public ResultActions getResultado() {
        return resultado;
    }

    public void setResultado(ResultActions resultado) {
        this.resultado = resultado;
    }
}
