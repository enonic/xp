package com.enonic.wem.api.account;

public final class GroupAccount
    extends NonUserAccount<GroupAccount>
{
    private GroupAccount( final AccountKey key )
    {
        super( key );
    }

    @Override
    public GroupAccount copy()
    {
        final GroupAccount target = new GroupAccount( getKey() );
        copyTo( target );
        return target;
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof GroupAccount ) && equals( (GroupAccount) o );
    }

    public static GroupAccount create( final String qName )
    {
        return new GroupAccount( AccountKey.group( qName ) );
    }
}
