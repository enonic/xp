package com.enonic.wem.api.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpdateResultTest
{

    @Test
    public void testFailure()
        throws Exception
    {
        final UpdateResult failure = UpdateResult.failure( "error message" );
        assertFalse( failure.successful() );
        assertTrue( failure.failed() );
        assertFalse( failure.isUpdated() );
        assertEquals( "error message", failure.failureCause() );
        assertNotNull( failure.toString() );
    }

    @Test
    public void testUpdated()
        throws Exception
    {
        final UpdateResult updated = UpdateResult.updated();
        assertTrue( updated.successful() );
        assertFalse( updated.failed() );
        assertTrue( updated.isUpdated() );
        assertNull( updated.failureCause() );
        assertNotNull( updated.toString() );
    }

    @Test
    public void testNotUpdated()
        throws Exception
    {
        final UpdateResult notUpdated = UpdateResult.notUpdated();
        assertTrue( notUpdated.successful() );
        assertFalse( notUpdated.failed() );
        assertFalse( notUpdated.isUpdated() );
        assertNull( notUpdated.failureCause() );
        assertNotNull( notUpdated.toString() );
    }

    @Test
    public void testEquals()
        throws Exception
    {
        final UpdateResult notUpdated = UpdateResult.notUpdated();
        final UpdateResult updated = UpdateResult.updated();
        final UpdateResult failure = UpdateResult.failure( "error message" );

        assertFalse( notUpdated.equals( updated ) );
        assertFalse( notUpdated.equals( failure ) );
        assertFalse( updated.equals( notUpdated ) );
        assertFalse( updated.equals( failure ) );
        assertFalse( failure.equals( updated ) );
        assertFalse( failure.equals( notUpdated ) );
        assertEquals( failure, UpdateResult.failure( "error message" ) );
        assertEquals( notUpdated, notUpdated );
        assertFalse( UpdateResult.failure( "error message" ).equals( "error message" ) );
    }

    @Test
    public void testHashCode()
        throws Exception
    {
        final UpdateResult notUpdated = UpdateResult.notUpdated();
        final UpdateResult updated = UpdateResult.updated();
        final UpdateResult failure = UpdateResult.failure( "error message" );

        assertFalse( notUpdated.hashCode() == updated.hashCode() );
        assertFalse( notUpdated.hashCode() == failure.hashCode() );
        assertFalse( updated.hashCode() == notUpdated.hashCode() );
        assertFalse( updated.hashCode() == failure.hashCode() );
        assertFalse( failure.hashCode() == updated.hashCode() );
        assertFalse( failure.hashCode() == notUpdated.hashCode() );
    }
}
