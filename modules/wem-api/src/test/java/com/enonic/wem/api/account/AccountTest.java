package com.enonic.wem.api.account;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AccountTest<T extends Account>
{
    protected abstract T create( String name );

    protected void testBasic( final T account )
    {
        assertEquals( "dummy", account.getDisplayName() );
        assertSame( account, account.displayName( "test" ) );
        assertEquals( "test", account.getDisplayName() );

        final DateTime now = DateTime.now();

        assertNull( account.getCreatedTime() );
        assertSame( account, account.createdTime( now ) );
        assertEquals( now, account.getCreatedTime() );

        assertNull( account.getModifiedTime() );
        assertSame( account, account.modifiedTime( now ) );
        assertEquals( now, account.getModifiedTime() );

        assertFalse( account.isEditable() );
        assertSame( account, account.editable( true ) );
        assertTrue( account.isEditable() );

        assertFalse( account.isDeleted() );
        assertSame( account, account.deleted( true ) );
        assertTrue( account.isDeleted() );
    }

    protected T testCopy( final T source )
    {
        final DateTime now = DateTime.now();

        source.createdTime( now );
        source.modifiedTime( now );
        source.displayName( "test" );
        source.deleted( true );
        source.editable( true );

        final Account copy = source.copy();
        assertNotNull( copy );

        assertEquals( now, copy.getCreatedTime() );
        assertEquals( now, copy.getModifiedTime() );
        assertEquals( "test", copy.getDisplayName() );
        assertTrue( copy.isDeleted() );
        assertTrue( copy.isEditable() );

        return typecast( source );
    }

    private T typecast( final T source )
    {
        return source;
    }

    @Test
    public void testHashCode()
    {
        final T account = create( "other:dummy" );
        assertEquals( account.getKey().hashCode(), account.hashCode() );
    }

    @Test
    public void testEquals()
    {
        final T a1 = create( "other:dummy" );
        final T a2 = create( "other:dummy" );
        final T a3 = create( "other:test" );

        assertTrue( a1.equals( a2 ) );
        assertTrue( a2.equals( a1 ) );
        assertFalse( a1.equals( a3 ) );
        assertFalse( a3.equals( a1 ) );
    }
}
