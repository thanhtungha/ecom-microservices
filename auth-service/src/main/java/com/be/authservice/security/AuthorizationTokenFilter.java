package com.be.authservice.security;

import com.be.authservice.exception.BaseException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class AuthorizationTokenFilter extends OncePerRequestFilter {

    private final AuthenticationProvider authenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {
        logger.info("BEGIN doFilterInternal()");

        String jwt = getJwtFromRequest(httpServletRequest);

        if (StringUtils.hasText(jwt)) {
            try {
                SecurityContextHolder.getContext().setAuthentication(
                        authenticationProvider.validateToken(jwt));
            } catch (BaseException e) {
                logger.error(
                        "Could not set user authentication in security context",
                        e);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
        logger.info("END doFilterInternal()");
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(
                "Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
