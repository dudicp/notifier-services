package com.patimer.notifier.service.authentication;

import org.apache.commons.lang.Validate;

public enum RoleType
{
    User("ROLE_USER"),
    Admin("ROLE_ADMIN");

    private String name;

    private RoleType(String name)
    {
        Validate.notEmpty(name);
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
