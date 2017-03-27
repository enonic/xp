package com.enonic.xp.task;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskIdTest
{
    @Test
    public void testEquals()
    {
        final TaskId id1 = TaskId.from( "123" );
        final TaskId id2 = TaskId.from( "123" );
        final TaskId id3 = TaskId.from( "321" );

        assertEquals( true, id1.equals( id2 ) );
        assertEquals( false, id1.equals( id3 ) );
        assertEquals( false, id1.equals( "test" ) );
    }

    @Test
    public void testHashCode()
    {
        final TaskId id1 = TaskId.from( "123" );
        final TaskId id2 = TaskId.from( "123" );
        final TaskId id3 = TaskId.from( "321" );

        assertEquals( id1.hashCode(), id2.hashCode() );
        assertNotEquals( id1.hashCode(), id3.hashCode() );
    }

    @Test
    public void testToString()
    {
        final TaskId id = TaskId.from( "123" );
        assertEquals( "123", id.toString() );
    }
}
