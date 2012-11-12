package com.enonic.wem.api.account;


public final class RoleKey
    extends AccountKey
{
    protected RoleKey( final String userStore, final String localName )
    {
        super( AccountType.ROLE, userStore, localName );
    }
}
