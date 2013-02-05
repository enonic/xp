package com.enonic.wem.api.account;

public final class RoleAccount
    extends NonUserAccount
{
    private RoleAccount( final RoleKey key )
    {
        super( key );
    }

    public static RoleAccount create( final String qName )
    {
        return create( RoleKey.from( qName ) );
    }

    public static RoleAccount create( final RoleKey key )
    {
        return new RoleAccount( key );
    }
}
