package com.enonic.xp.admin.impl.rest.resource.auth;

import com.enonic.xp.security.IdProviderKey;

public final class LoginRequest
{
    protected boolean rememberMe;

    protected String user;

    protected String password;

    protected String idProvider;

    public String getUser()
    {
        return user;
    }

    public IdProviderKey getIdProvider()
    {
        return idProvider == null ? null : IdProviderKey.from( idProvider );
    }

    public String getPassword()
    {
        return password;
    }

    public boolean isRememberMe()
    {
        return rememberMe;
    }
}
