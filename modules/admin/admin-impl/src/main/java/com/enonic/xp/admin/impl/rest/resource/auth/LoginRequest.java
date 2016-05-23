package com.enonic.xp.admin.impl.rest.resource.auth;

import com.enonic.xp.security.UserStoreKey;

public final class LoginRequest
{
    protected boolean rememberMe;

    protected String user;

    protected String password;

    protected String userStore;

    public String getUser()
    {
        return user;
    }

    public UserStoreKey getUserStore()
    {
        return userStore == null ? null : UserStoreKey.from( userStore );
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
