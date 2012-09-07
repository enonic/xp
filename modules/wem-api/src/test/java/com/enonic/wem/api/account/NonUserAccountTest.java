package com.enonic.wem.api.account;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class NonUserAccountTest<T extends NonUserAccount>
    extends AccountTest<T>
{
    @Override
    protected AccountKey createIllegalKey( final String qName )
    {
        return AccountKey.user( qName );
    }

    @Test
    public void testMembers()
    {
        final T account = create( "other:dummy" );
        assertNull( account.getMembers() );

        final AccountKeys members = AccountKeys.empty();
        account.setMembers( members );
        assertSame( members, account.getMembers() );
    }
}
