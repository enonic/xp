package com.enonic.xp.task;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskProgressTest
{
    @Test
    public void testAccessors()
    {
        final TaskProgress p = TaskProgress.create().info( "test" ).current( 30 ).total( 100 ).build();

        assertEquals( "test", p.getInfo() );
        assertEquals( 30, p.getCurrent() );
        assertEquals( 100, p.getTotal() );
    }

    @Test
    public void testCopy()
    {
        final TaskProgress p1 = TaskProgress.create().current( 0 ).total( 100 ).build();
        final TaskProgress p2 = p1.copy().current( 30 ).build();

        assertEquals( false, p1.equals( p2 ) );
    }

    @Test
    public void testEquals()
    {
        final TaskProgress p1 = TaskProgress.create().current( 0 ).total( 100 ).build();
        final TaskProgress p2 = TaskProgress.create().current( 0 ).total( 100 ).build();
        final TaskProgress p3 = TaskProgress.create().current( 30 ).total( 100 ).build();

        assertEquals( true, p1.equals( p2 ) );
        assertEquals( false, p1.equals( p3 ) );
        assertEquals( false, p1.equals( "test" ) );
    }

    @Test
    public void testHashCode()
    {
        final TaskProgress p1 = TaskProgress.create().current( 0 ).total( 100 ).build();
        final TaskProgress p2 = TaskProgress.create().current( 0 ).total( 100 ).build();
        final TaskProgress p3 = TaskProgress.create().current( 30 ).total( 100 ).build();

        assertEquals( p1.hashCode(), p2.hashCode() );
        assertNotEquals( p1.hashCode(), p3.hashCode() );
    }

    @Test
    public void testToString()
    {
        final TaskProgress p = TaskProgress.create().info( "test" ).current( 0 ).total( 100 ).build();
        assertEquals( "TaskProgress{current=0, total=100, info=test}", p.toString() );
    }
}
