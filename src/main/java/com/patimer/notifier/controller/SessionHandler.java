package com.patimer.notifier.controller;

import com.patimer.notifier.service.authentication.RoleType;
import com.patimer.notifier.service.authentication.SessionPrincipal;
import com.patimer.notifier.service.exception.InvalidSessionException;
import org.apache.commons.lang.Validate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class SessionHandler
{
    public SessionPrincipal verify(Principal principal) throws InvalidSessionException
    {
        if(principal == null){
            throw new InvalidSessionException("principal not found (null reference).");
        }

        if(principal instanceof UsernamePasswordAuthenticationToken)
        {
            Object innerPrincipal = ((UsernamePasswordAuthenticationToken)principal).getPrincipal();

            if(innerPrincipal != null && innerPrincipal instanceof SessionPrincipal) {
                return (SessionPrincipal)innerPrincipal;
            }
            else if(innerPrincipal != null){
                throw new InvalidSessionException("Unsupported inner-principal class: '" + innerPrincipal.getClass() + "'.");
            }
            else {
                throw new InvalidSessionException("inner-principal not found (null reference).");
            }
        }
        else{
            throw new InvalidSessionException("Unsupported principal class: '" + principal.getClass() + "'.");
        }
    }

    public Authentication createAuthenticationFromSessionPrincipal(SessionPrincipal sessionPrincipal)
    {
        Validate.notNull(sessionPrincipal);
        List<GrantedAuthority> grantedAuthorities = convertSessionPrincipalToGrantedAuthorities(sessionPrincipal);
        return new UsernamePasswordAuthenticationToken(sessionPrincipal, null /*credentials*/, grantedAuthorities);
    }

    private List<GrantedAuthority> convertSessionPrincipalToGrantedAuthorities(SessionPrincipal sessionPrincipal)
    {
        Validate.notNull(sessionPrincipal);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if(sessionPrincipal.isAdmin()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(RoleType.Admin.getName()));
        }
        else {
            grantedAuthorities.add(new SimpleGrantedAuthority(RoleType.User.getName()));

        }
        return grantedAuthorities;
    }

    public boolean supports(Class<?> authenticationClass)
    {
        return authenticationClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
