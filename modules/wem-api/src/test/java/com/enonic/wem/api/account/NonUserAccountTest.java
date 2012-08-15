package com.enonic.wem.api.account;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class NonUserAccountTest<T extends NonUserAccount>
    extends AccountTest<T>
{
    @Override
    protected void testBasic( final T account )
    {
        super.testBasic( account );

        assertNotNull( account.getMembers() );
        assertEquals( 0, account.getMembers().getSize() );

        final AccountKeySet set = AccountKeySet.from( "user:other:dummy" );
        assertSame( account, account.members( set ) );
        assertSame( set, account.getMembers() );

        assertSame( account, account.members( null ) );
        assertNotNull( account.getMembers() );
        assertEquals( 0, account.getMembers().getSize() );
    }

    @Test
    public void testCopy()
    {
        final AccountKeySet set = AccountKeySet.from( "user:other:dummy" );

        final T account = create( "other:dummy" );
        account.members( set );

        final T copy = testCopy( account );

        assertSame( set, copy.getMembers() );
    }
}
