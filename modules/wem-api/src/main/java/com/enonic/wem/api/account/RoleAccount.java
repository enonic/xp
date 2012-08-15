package com.enonic.wem.api.account;

public final class RoleAccount
    extends NonUserAccount<RoleAccount>
{
    private RoleAccount( final AccountKey key )
    {
        super( key );
    }

    @Override
    public RoleAccount copy()
    {
        final RoleAccount target = new RoleAccount( getKey() );
        copyTo( target );
        return target;
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof RoleAccount ) && equals( (RoleAccount) o );
    }

    public static RoleAccount create( final String qName )
    {
        return new RoleAccount( AccountKey.role( qName ) );
    }
}
