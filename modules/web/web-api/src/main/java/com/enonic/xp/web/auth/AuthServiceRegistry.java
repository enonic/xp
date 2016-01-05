package com.enonic.xp.web.auth;

import java.util.Collection;

public interface AuthServiceRegistry
{
    Collection<AuthService> getAuthServices();

    AuthService getAuthService( final String key );
}
