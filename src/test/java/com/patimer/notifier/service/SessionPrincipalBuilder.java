package com.patimer.notifier.service;

import com.patimer.notifier.service.authentication.SessionPrincipal;

import java.util.UUID;

public class SessionPrincipalBuilder
{
    private UUID accountId = UUID.randomUUID();
    private String mail = "user1@gmail.com";
    private boolean isAdmin = false;

    public SessionPrincipalBuilder withAccountId(UUID accountId)
    {
        this.accountId = accountId;
        return this;
    }

    public SessionPrincipalBuilder withMail(String mail)
    {
        this.mail = mail;
        return this;
    }

    public SessionPrincipalBuilder withIsAdmin(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
        return this;
    }

    public SessionPrincipal build()
    {
        return new SessionPrincipal(accountId, mail, isAdmin);
    }
}
