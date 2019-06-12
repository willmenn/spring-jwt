package com.spring.jwt;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;

import static com.spring.jwt.JwtConstants.JWT_SEPARATOR;
import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter(PRIVATE)
class JwtContext {
    private static final int HEADER_INDEX = 0;
    private static final int PAYLOAD_INDEX = 1;
    private static final int SECRET_INDEX = 2;
    private String headerBase64;
    private String payloadBase64;
    private String secretBase64;

    JwtContext(String body) {
        String[] content = body.split(JWT_SEPARATOR);

        this.headerBase64 = content[HEADER_INDEX];
        this.payloadBase64 = content[PAYLOAD_INDEX];
        this.secretBase64 = content[SECRET_INDEX];
    }

    static String removeBase64(String base64) {
        return new String((Base64.decodeBase64(base64)));
    }
}
