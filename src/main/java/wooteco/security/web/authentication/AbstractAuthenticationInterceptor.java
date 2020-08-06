package wooteco.security.web.authentication;

import wooteco.security.core.authentication.AuthenticationException;
import wooteco.security.core.authentication.AuthenticationManager;
import wooteco.security.core.authentication.AuthenticationToken;
import wooteco.security.core.Authentication;
import wooteco.security.web.authentication.handler.AuthenticationFailureHandler;
import wooteco.security.web.authentication.handler.AuthenticationSuccessHandler;
import wooteco.security.core.context.SecurityContextHolder;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractAuthenticationInterceptor implements HandlerInterceptor {
    private AuthenticationManager authenticationManager;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;

    protected AbstractAuthenticationInterceptor(AuthenticationManager authenticationManager, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        this.authenticationManager = authenticationManager;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        try {
            Authentication authentication = attemptAuthentication(request, response);
            successfulAuthentication(request, response, authentication);
        } catch (AuthenticationException e) {
            unsuccessfulAuthentication(request, response, e);
        }

        return false;
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        successHandler.onAuthenticationSuccess(request, response, authentication);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed) throws IOException {
        SecurityContextHolder.clearContext();

        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationToken authenticationToken = convert(request);
        return authenticationManager.authenticate(authenticationToken);
    }

    protected abstract AuthenticationToken convert(HttpServletRequest request);
}
