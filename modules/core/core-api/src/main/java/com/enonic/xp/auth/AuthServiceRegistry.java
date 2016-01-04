package com.enonic.xp.auth;

import java.util.Collection;

public interface AuthServiceRegistry
{
    Collection<AuthService> getAuthServices();
}
