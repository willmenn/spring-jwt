package com.spring.jwt;

import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
public class JWTFilter implements Filter {

    private JWTValidator jwtValidator;

    public JWTFilter() {
        this.jwtValidator = new JWTValidator();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String body = IOUtils.toString(req.getReader());

        try {
            if (jwtValidator.validate(body)) {
                chain.doFilter(request, response);
            }else {
                throw new JwtException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
