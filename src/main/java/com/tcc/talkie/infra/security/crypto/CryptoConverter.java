package com.tcc.talkie.infra.security.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.*;

@Converter
@Component
public class CryptoConverter implements AttributeConverter<String, String> {

    @Autowired
    private CryptoService cryptoService;

    @Override
    public String convertToDatabaseColumn(String attribute){
        if (attribute == null) return null;
        return cryptoService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData){
        if(dbData == null) return null;
        return cryptoService.decrypt(dbData);
    }
}
