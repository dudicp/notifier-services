package com.patimer.notifier.service.authentication;

import com.patimer.notifier.controller.SessionHandler;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


@Component
public class CustomAuthenticationProviderImpl implements AuthenticationProvider
{
    private AuthenticationService authenticationService;
    private SessionHandler sessionHandler;

    @Autowired
    public CustomAuthenticationProviderImpl(AuthenticationService authenticationService, SessionHandler sessionHandler)
    {
        Validate.notNull(authenticationService);
        Validate.notNull(sessionHandler);
        this.authenticationService = authenticationService;
        this.sessionHandler = sessionHandler;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        SessionPrincipal sessionPrincipal = authenticationService.authenticate(username, password);
        return sessionHandler.createAuthenticationFromSessionPrincipal(sessionPrincipal);
    }

    public boolean supports(Class<?> authenticationClass)
    {
        return sessionHandler.supports(authenticationClass);
    }
}
