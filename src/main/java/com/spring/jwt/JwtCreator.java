package com.spring.jwt;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.spring.jwt.JwtConstants.JWT_SEPARATOR;
import static java.nio.charset.StandardCharsets.UTF_8;

class JwtCreator {

    private static final String SHA_256 = "HmacSHA256";

    String createJwtToken(String header, String payload, String secret) throws InvalidKeyException,
            NoSuchAlgorithmException {
        String headerBase64 = Base64.encodeBase64URLSafeString(header.getBytes());
        String payloadBase64 = Base64.encodeBase64URLSafeString(payload.getBytes());

        String securityHex = createSignature(secret, headerBase64, payloadBase64);

        return headerBase64
                .concat(JWT_SEPARATOR)
                .concat(payloadBase64)
                .concat(JWT_SEPARATOR)
                .concat(securityHex);
    }

    private String createSignature(String secret, String headerBase64, String payloadBase64)
            throws NoSuchAlgorithmException, InvalidKeyException {

        final SecretKeySpec secret_key = new SecretKeySpec(UTF_8.encode(secret).array(), SHA_256);
        final Mac mac = Mac.getInstance(SHA_256);
        mac.init(secret_key);
        mac.update(headerBase64.getBytes());
        mac.update((byte) '.');
        byte[] bytes = mac.doFinal(payloadBase64.getBytes());
        return Base64.encodeBase64URLSafeString(bytes);
    }
}
