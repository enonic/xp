package com.enonic.xp.task;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskIdTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( TaskId.class ).withNonnullFields( "value" ).verify();
    }

    @Test
    void testToString()
    {
        final TaskId id = TaskId.from( "123" );
        assertEquals( "123", id.toString() );
    }
}
