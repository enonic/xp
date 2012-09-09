package com.enonic.wem.api.userstore;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UserStoreNameTest
{
    @Test
    public void testCreate()
    {
        final UserStoreName name = UserStoreName.from( "test" );
        assertNotNull( name );
        assertEquals( "test", name.toString() );
        assertEquals( false, name.isSystem() );
    }

    @Test
    public void testSystem()
    {
        final UserStoreName name = UserStoreName.system();
        assertNotNull( name );
        assertEquals( "system", name.toString() );
        assertEquals( true, name.isSystem() );
    }

    @Test
    public void testHashCode()
    {
        final UserStoreName name1 = UserStoreName.from( "test" );
        final UserStoreName name2 = UserStoreName.system();
        final UserStoreName name3 = UserStoreName.from( "test" );

        assertTrue( name1.hashCode() == name3.hashCode() );
        assertFalse( name1.hashCode() == name2.hashCode() );
    }

    @Test
    public void testEquals()
    {
        final UserStoreName name1 = UserStoreName.from( "test" );
        final UserStoreName name2 = UserStoreName.system();
        final UserStoreName name3 = UserStoreName.from( "test" );

        assertTrue( name1.equals( name3 ) );
        assertFalse( name1.equals( name2 ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalName_empty()
    {
        UserStoreName.from( "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalName_null()
    {
        UserStoreName.from( null );
    }
}
