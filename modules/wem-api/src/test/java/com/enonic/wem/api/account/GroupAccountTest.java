package com.enonic.wem.api.account;

public class GroupAccountTest
    extends NonUserAccountTest<GroupAccount>
{
    @Override
    protected GroupAccount create( final String qName )
    {
        return GroupAccount.create( qName );
    }

    @Override
    protected AccountKey createKey( final String qName )
    {
        return AccountKey.group( qName );
    }

    @Override
    protected GroupAccount create( final AccountKey key )
    {
        return GroupAccount.create( key );
    }
}
