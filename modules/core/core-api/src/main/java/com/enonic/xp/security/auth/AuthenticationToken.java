package com.enonic.xp.security.auth;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.IdProviderKey;

@Beta
public abstract class AuthenticationToken
{
    private IdProviderKey idProvider;

    private boolean rememberMe;

    public final IdProviderKey getIdProvider()
    {
        return this.idProvider;
    }

    public final void setIdProvider( final IdProviderKey idProvider )
    {
        this.idProvider = idProvider;
    }

    public final boolean isRememberMe()
    {
        return this.rememberMe;
    }

    public final void setRememberMe( final boolean rememberMe )
    {
        this.rememberMe = rememberMe;
    }
}
