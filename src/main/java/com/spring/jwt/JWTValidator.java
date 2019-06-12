package com.spring.jwt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.spring.jwt.JwtContext.removeBase64;

class JWTValidator {
    private JwtCreator creator;

    JWTValidator() {
        this.creator = new JwtCreator();
    }

    boolean validate(String body) throws NoSuchAlgorithmException, InvalidKeyException {
        JwtContext jwtContext = new JwtContext(body);

        String jwtToken = creator.createJwtToken(jwtContext.getHeaderBase64(),
                jwtContext.getPayloadBase64(), jwtContext.getSecretBase64());

        String expectedToken = creator.createJwtToken(removeBase64(jwtContext.getHeaderBase64()),
                removeBase64(jwtContext.getPayloadBase64()),
                removeBase64(jwtContext.getSecretBase64()));

        return isTokenTheSame(jwtToken, expectedToken);
    }

    private boolean isTokenTheSame(String jwtToken, String expectedToken) {
        return expectedToken.equals(jwtToken);
    }
}
