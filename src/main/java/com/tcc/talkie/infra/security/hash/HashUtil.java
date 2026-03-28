package com.tcc.talkie.infra.security.hash;

import java.security.MessageDigest;
import java.util.Base64;

public class HashUtil {

    public static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(value.getBytes());

            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash", e);
        }
    }

    public static String hashCpf(String cpf) {
        if (cpf == null) return null;

        String cpfLimpo = cpf.replaceAll("\\D", "");

        return hash(cpfLimpo);
    }
}