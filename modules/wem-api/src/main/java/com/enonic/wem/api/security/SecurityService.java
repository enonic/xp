package com.enonic.wem.api.security;

public interface SecurityService
{
    UserStores getUserStores();

    Principals getPrincipals( UserStoreKey useStore, PrincipalType type );
}
