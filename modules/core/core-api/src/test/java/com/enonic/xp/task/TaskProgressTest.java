package com.enonic.xp.task;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class TaskProgressTest
{
    @Test
    void testAccessors()
    {
        final TaskProgress p = TaskProgress.create().info( "test" ).current( 30 ).total( 100 ).build();

        assertEquals( "test", p.getInfo() );
        assertEquals( 30, p.getCurrent() );
        assertEquals( 100, p.getTotal() );
    }

    @Test
    void testCopy()
    {
        final TaskProgress p1 = TaskProgress.create().current( 0 ).total( 100 ).build();
        final TaskProgress p2 = p1.copy().current( 30 ).build();

        assertFalse( p1.equals( p2 ) );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( TaskProgress.class ).withNonnullFields( "info" ).verify();
    }

    @Test
    void testToString()
    {
        final TaskProgress p = TaskProgress.create().info( "test" ).current( 0 ).total( 100 ).build();
        assertEquals( "TaskProgress{current=0, total=100, info=test}", p.toString() );
    }
}
