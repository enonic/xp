package com.enonic.wem.api.account;


public final class RoleKey
    extends AccountKey
{
    protected RoleKey( final String userStore, final String localName )
    {
        super( AccountType.ROLE, userStore, localName );
    }

    public static RoleKey from( final String qualifiedName )
    {
        return from( AccountType.ROLE, qualifiedName ).asRole();
    }
}
