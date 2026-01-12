package com.essence.essencebackend.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtLocalConfig {

    // obtiene la secretkey de jwt
    @Bean
    SecretKey jwtSecretKey(@Value("${application.security.jwt.secret-key}") String secretB64) {
        byte[] keyBytes = Base64.getDecoder().decode(secretB64);

        // RFC 7518
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("HS256 requiere una clave >= 256 bits (32 bytes)");
        }
        // HS256 => HmacSHA256
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }
    // emite y firma el token
    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        // NimbusJwtEncoder implementa JwtEncoder (encode)
        return NimbusJwtEncoder.withSecretKey(jwtSecretKey).algorithm(MacAlgorithm.HS256)
                .build();
    }
    // valida el token
    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256) // HS256/384/512
                .build();
    }
}
