package com.enonic.wem.security;

public interface SecurityService
{
    UserStores getUserStores();

    Principals getPrincipals( UserStoreKey useStore, PrincipalType type );
}
