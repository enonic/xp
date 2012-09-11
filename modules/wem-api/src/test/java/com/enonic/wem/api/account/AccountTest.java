package com.enonic.wem.api.account;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class AccountTest<T extends Account>
{
    protected abstract T create( String qName );

    protected abstract T create( AccountKey key );

    protected abstract AccountKey createKey( String qName );

    protected abstract AccountKey createIllegalKey( String qName );

    @Test
    public void testCreate()
    {
        final T account1 = create( "other:dummy" );
        assertNotNull( account1 );

        final T account2 = create( createKey( "other:dummy" ) );
        assertNotNull( account2 );

        assertFalse( account1 == account2 );
        assertEquals( account1.getKey(), account2.getKey() );
    }

    @Test
    public void testKey()
    {
        final T account = create( "other:dummy" );
        final AccountKey key = createKey( "other:dummy" );

        assertNotNull( account.getKey() );
        assertEquals( key, account.getKey() );
    }

    @Test
    public void testDisplayName()
    {
        final T account = create( "other:dummy" );
        assertEquals( "dummy", account.getDisplayName() );

        account.setDisplayName( "Test" );
        assertEquals( "Test", account.getDisplayName() );

        account.setDisplayName( null );
        assertEquals( "dummy", account.getDisplayName() );
    }

    @Test
    public void testCreatedTime()
    {
        final T account = create( "other:dummy" );
        assertNull( account.getCreatedTime() );

        final DateTime now = DateTime.now();
        account.setCreatedTime( now );
        assertEquals( now, account.getCreatedTime() );
    }

    @Test
    public void testModifiedTime()
    {
        final T account = create( "other:dummy" );
        assertNull( account.getModifiedTime() );

        final DateTime now = DateTime.now();
        account.setModifiedTime( now );
        assertEquals( now, account.getModifiedTime() );
    }

    @Test
    public void testDeleted()
    {
        final T account = create( "other:dummy" );
        assertFalse( account.isDeleted() );

        account.setDeleted( true );
        assertTrue( account.isDeleted() );
    }

    @Test
    public void testEditable()
    {
        final T account = create( "other:dummy" );
        assertFalse( account.isEditable() );

        account.setEditable( true );
        assertTrue( account.isEditable() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_illegalKey()
    {
        create( createIllegalKey( "other:dummy" ) );
    }
}
