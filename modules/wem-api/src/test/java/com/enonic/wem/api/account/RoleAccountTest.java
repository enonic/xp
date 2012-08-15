package com.enonic.wem.api.account;

import org.junit.Test;

import static org.junit.Assert.*;

public class RoleAccountTest
    extends NonUserAccountTest<RoleAccount>
{
    @Override
    protected RoleAccount create( final String name )
    {
        return RoleAccount.create( name );
    }

    @Test
    public void testBasic()
    {
        final RoleAccount account = create( "other:dummy" );
        assertNotNull( account );
        assertNotNull( account.getKey() );
        assertEquals( "role:other:dummy", account.getKey().toString() );

        testBasic( account );
    }
}
