package com.enonic.wem.api.account;

import org.junit.Test;

import static org.junit.Assert.*;

public class GroupAccountTest
    extends NonUserAccountTest<GroupAccount>
{
    @Override
    protected GroupAccount create( final String name )
    {
        return GroupAccount.create( name );
    }

    @Test
    public void testBasic()
    {
        final GroupAccount account = create( "other:dummy" );
        assertNotNull( account );
        assertNotNull( account.getKey() );
        assertEquals( "group:other:dummy", account.getKey().toString() );

        testBasic( account );
    }
}
