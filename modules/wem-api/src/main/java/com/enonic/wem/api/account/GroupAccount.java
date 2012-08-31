package com.enonic.wem.api.account;

import com.google.common.base.Preconditions;

public final class GroupAccount
    extends NonUserAccount
{
    private GroupAccount( final AccountKey key )
    {
        super( key );
    }

    public static GroupAccount create( final String qName )
    {
        return create( AccountKey.group( qName ) );
    }

    public static GroupAccount create( final AccountKey key )
    {
        Preconditions.checkArgument( key.isGroup(), "Account key must be of type group" );
        return new GroupAccount( key );
    }
}
