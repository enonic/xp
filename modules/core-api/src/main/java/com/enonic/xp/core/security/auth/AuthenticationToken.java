package com.enonic.xp.core.security.auth;

import com.enonic.xp.core.security.UserStoreKey;

public abstract class AuthenticationToken
{
    private UserStoreKey userStore;

    private boolean rememberMe;

    public final UserStoreKey getUserStore()
    {
        return this.userStore;
    }

    public final void setUserStore( final UserStoreKey userStore )
    {
        this.userStore = userStore;
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
