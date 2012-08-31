package com.enonic.wem.api.account;

import com.google.common.base.Preconditions;

public final class RoleAccount
    extends NonUserAccount
{
    private RoleAccount( final AccountKey key )
    {
        super( key );
    }

    public static RoleAccount create( final String qName )
    {
        return create( AccountKey.role( qName ) );
    }

    public static RoleAccount create( final AccountKey key )
    {
        Preconditions.checkArgument( key.isRole(), "Account key must be of type role" );
        return new RoleAccount( key );
    }
}
