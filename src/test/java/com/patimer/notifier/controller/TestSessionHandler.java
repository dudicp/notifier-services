package com.patimer.notifier.controller;

import com.patimer.notifier.service.SessionPrincipalBuilder;
import com.patimer.notifier.service.authentication.RoleType;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import com.patimer.notifier.service.exception.InvalidSessionException;
import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.Collections;

public class TestSessionHandler
{
    private SessionHandler sessionHandler = new SessionHandler();

    @Test
    public void testVerifyWithValidPrincipal() throws InvalidSessionException
    {
        // given
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withIsAdmin(false).build();
        Principal principal = sessionHandler.createAuthenticationFromSessionPrincipal(sessionPrincipal);

        // when
        SessionPrincipal returnedSessionPrincipal = sessionHandler.verify(principal);

        // then
        Assert.notNull(returnedSessionPrincipal);
        Assert.isTrue(sessionPrincipal.equals(returnedSessionPrincipal));
    }

    @Test(expected = InvalidSessionException.class)
    public void testVerifyWithNullPrincipal() throws InvalidSessionException
    {
        // given

        // when
        sessionHandler.verify(null);

        // then - expected exception
    }

    @Test(expected = InvalidSessionException.class)
    public void testVerifyInvalidPrincipal() throws InvalidSessionException
    {
        // given
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withIsAdmin(false).build();
        Principal principal =
            new AnonymousAuthenticationToken(
                "key",
                sessionPrincipal,
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.User.getName()))
            );

        // when
        sessionHandler.verify(principal);

        // then - expected exception
    }

    @Test(expected = InvalidSessionException.class)
    public void testVerifyWithNullInnerPrincipal() throws InvalidSessionException
    {
        // given
        Principal principal =
            new UsernamePasswordAuthenticationToken(
                null, /*principal*/
                null, /*credentials*/
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.User.getName()))
            );

        // when
        sessionHandler.verify(principal);

        // then - expected exception
    }

    @Test(expected = InvalidSessionException.class)
    public void testVerifyWithInvalidInnerPrincipal() throws InvalidSessionException
    {
        // given
        Principal principal =
            new UsernamePasswordAuthenticationToken(
                "principal", /*principal*/
                null, /*credentials*/
                Collections.singletonList(new SimpleGrantedAuthority(RoleType.User.getName()))
            );

        // when
        sessionHandler.verify(principal);

        // then - expected exception
    }

    @Test
    public void testCreateWithUserSessionPrincipal()
    {
        // given
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withIsAdmin(false).build();

        // when
        Authentication authentication = sessionHandler.createAuthenticationFromSessionPrincipal(sessionPrincipal);

        // then
        assertValidAuthentication(authentication, sessionPrincipal, RoleType.User);

    }

    @Test
    public void testCreateWithAdminSessionPrincipal()
    {
        // given
        SessionPrincipal sessionPrincipal = new SessionPrincipalBuilder().withIsAdmin(true).build();

        // when
        Authentication authentication = sessionHandler.createAuthenticationFromSessionPrincipal(sessionPrincipal);

        // then
        assertValidAuthentication(authentication, sessionPrincipal, RoleType.Admin);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNullSessionPrincipal()
    {
        // given

        // when
        sessionHandler.createAuthenticationFromSessionPrincipal(null);

        // then - expect exception
    }

    private void assertValidAuthentication(Authentication authentication, SessionPrincipal sessionPrincipal, RoleType roleType)
    {
        Assert.notNull(authentication);
        Assert.isTrue(authentication instanceof UsernamePasswordAuthenticationToken);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken)authentication;
        Assert.isNull(usernamePasswordAuthenticationToken.getCredentials());
        Assert.isTrue(sessionPrincipal.equals(usernamePasswordAuthenticationToken.getPrincipal()));
        Assert.isTrue(usernamePasswordAuthenticationToken.getAuthorities().size() == 1);
        Assert.isTrue(usernamePasswordAuthenticationToken.getAuthorities().contains(new SimpleGrantedAuthority(roleType.getName())));
    }
}
