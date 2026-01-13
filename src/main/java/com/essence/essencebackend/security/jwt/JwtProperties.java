package com.essence.essencebackend.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*  Definimos las properties para el secretKey y expiration.
    Para asi declararlos en el archivo properties.
    También agrega la @ en el main.
*/
@ConfigurationProperties(prefix = "application.security.jwt")
public record JwtProperties(
        String secretKey,
        long expirationSeconds
) {}
