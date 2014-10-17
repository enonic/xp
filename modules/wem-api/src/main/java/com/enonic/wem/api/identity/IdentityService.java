package com.enonic.wem.api.identity;

public interface IdentityService
{
    Realms getRealms();

    Identities getIdentities( RealmKey realm, IdentityType type );
}
