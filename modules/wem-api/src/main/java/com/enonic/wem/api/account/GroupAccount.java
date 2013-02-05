package com.enonic.wem.api.account;

public final class GroupAccount
    extends NonUserAccount
{
    private GroupAccount( final GroupKey key )
    {
        super( key );
    }

    public static GroupAccount create( final String qName )
    {
        return create( GroupKey.from( qName ) );
    }

    public static GroupAccount create( final GroupKey key )
    {
        return new GroupAccount( key );
    }
}
