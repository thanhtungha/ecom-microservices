package com.be.authservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.be.authservice.exception.RestExceptions;
import com.be.authservice.model.User;
import com.be.authservice.repository.IAuthRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final IAuthRepository repository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String account) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 60 * 60 * 1000 * 24);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create().withIssuer(account).withIssuedAt(now).withExpiresAt(
                validity).sign(algorithm);
    }

    public Authentication validateToken(String jwt) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decoded = verifier.verify(jwt);
        Optional<User> user = repository.findByUserName(decoded.getIssuer());
        User userInfo = user.orElse(null);
        if (userInfo != null && jwt.equals(userInfo.getAccessToken())) {
            return new UsernamePasswordAuthenticationToken(userInfo,
                    userInfo.getUserPassword(),
                    Collections.emptyList());
        } else {
            throw new RestExceptions.Unauthorized("Invalid access token");
        }
    }

}
