package com.patimer.notifier.service.authentication;

import org.springframework.security.authentication.BadCredentialsException;

public interface AuthenticationService
{
    SessionPrincipal authenticate(String name, String password) throws BadCredentialsException;
}
