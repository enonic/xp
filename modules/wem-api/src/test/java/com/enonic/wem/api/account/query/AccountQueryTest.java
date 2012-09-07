package com.enonic.wem.api.account.query;

import org.junit.Test;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.query.AccountQuery;

import static org.junit.Assert.*;

public class AccountQueryTest
{
    @Test
    public void testQuery()
    {
        final AccountQuery query1 = new AccountQuery( null );
        assertEquals( "", query1.getQuery() );

        final AccountQuery query2 = new AccountQuery( "text" );
        assertEquals( "text", query2.getQuery() );
    }

    @Test
    public void testSort()
    {
        final AccountQuery query = new AccountQuery( "text" );
        assertNull( query.getSortField() );
        assertEquals( AccountQuery.Direction.ASC, query.getSortDirection() );

        assertSame( query, query.sortAsc( "field" ) );
        assertEquals( "field", query.getSortField() );
        assertEquals( AccountQuery.Direction.ASC, query.getSortDirection() );

        assertSame( query, query.sortDesc( "field" ) );
        assertEquals( "field", query.getSortField() );
        assertEquals( AccountQuery.Direction.DESC, query.getSortDirection() );
    }

    @Test
    public void testTypes()
    {
        final AccountQuery query = new AccountQuery( "text" );
        assertNotNull( query.getTypes() );
        assertTrue( query.getTypes().contains( AccountType.USER ) );
        assertTrue( query.getTypes().contains( AccountType.GROUP ) );
        assertTrue( query.getTypes().contains( AccountType.ROLE ) );

        assertSame( query, query.types( AccountType.USER ) );
        assertNotNull( query.getTypes() );
        assertTrue( query.getTypes().contains( AccountType.USER ) );
        assertFalse( query.getTypes().contains( AccountType.GROUP ) );
        assertFalse( query.getTypes().contains( AccountType.ROLE ) );
    }

    @Test
    public void testUserStores()
    {
        final AccountQuery query = new AccountQuery( "text" );
        assertNotNull( query.getUserStores() );
        assertEquals( 0, query.getUserStores().size() );

        assertSame( query, query.userStores() );
        assertNotNull( query.getUserStores() );
        assertEquals( 0, query.getUserStores().size() );

        assertSame( query, query.userStores( "other", "corporation" ) );
        assertNotNull( query.getUserStores() );
        assertEquals( 2, query.getUserStores().size() );
        assertTrue( query.getUserStores().contains( "other" ) );
        assertTrue( query.getUserStores().contains( "corporation" ) );
    }

    @Test
    public void testLimit()
    {
        final AccountQuery query = new AccountQuery( "text" );
        assertEquals( 10, query.getLimit() );

        assertSame( query, query.limit( 20 ) );
        assertEquals( 20, query.getLimit() );
    }

    @Test
    public void testOffset()
    {
        final AccountQuery query = new AccountQuery( "text" );
        assertEquals( 0, query.getOffset() );

        assertSame( query, query.offset( 20 ) );
        assertEquals( 20, query.getOffset() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid_limit()
    {
        final AccountQuery query = new AccountQuery( "text" );
        query.limit( -1 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid_offset()
    {
        final AccountQuery query = new AccountQuery( "text" );
        query.offset( -1 );
    }

    @Test
    public void testHashCode()
    {
        final AccountQuery query1 = new AccountQuery();
        final AccountQuery query2 = new AccountQuery();

        assertTrue( query1.hashCode() == query2.hashCode() );

        query1.email( "test" );
        assertFalse( query1.hashCode() == query2.hashCode() );

        query2.email( "test" );
        assertTrue( query1.hashCode() == query2.hashCode() );
    }

    @Test
    public void testEquals()
    {
        final AccountQuery query1 = new AccountQuery();
        final AccountQuery query2 = new AccountQuery();

        assertFalse( query1.equals( "test" ) );
        assertTrue( query1.equals( query1 ) );
        assertTrue( query1.equals( query2 ) );

        query1.email( "test" );
        assertFalse( query1.equals( query2 ) );

        query2.email( "test" );
        assertTrue( query1.equals( query2 ) );
    }
}
