package com.enonic.wem.api.account;

public class RoleAccountTest
    extends NonUserAccountTest<RoleAccount>
{
    @Override
    protected RoleAccount create( final String qName )
    {
        return RoleAccount.create( qName );
    }

    @Override
    protected AccountKey createKey( final String qName )
    {
        return RoleKey.from( qName );
    }

    @Override
    protected RoleAccount create( final AccountKey key )
    {
        return RoleAccount.create( key.asRole() );
    }
}
