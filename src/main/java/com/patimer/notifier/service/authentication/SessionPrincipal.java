package com.patimer.notifier.service.authentication;

import java.util.UUID;

public class SessionPrincipal
{
    private UUID accountId;
    private String email;
    private boolean isAdmin;

    public SessionPrincipal(UUID accountId, String email, boolean isAdmin)
    {
        this.accountId = accountId;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public UUID getAccountId()
    {
        return accountId;
    }

    public String getEmail()
    {
        return email;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SessionPrincipal that = (SessionPrincipal) o;

        if (isAdmin != that.isAdmin) return false;
        if (!accountId.equals(that.accountId)) return false;
        return email.equals(that.email);

    }

    @Override
    public int hashCode()
    {
        int result = accountId.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + (isAdmin ? 1 : 0);
        return result;
    }
}
