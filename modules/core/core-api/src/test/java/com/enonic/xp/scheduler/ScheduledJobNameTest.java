package com.enonic.xp.scheduler;


import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduledJobNameTest
{
    @Test
    void testCannotBeNull()
    {
        assertThrows( NullPointerException.class, () -> ScheduledJobName.from( null ) );
        assertThrows( NullPointerException.class, () -> ScheduledJobName.create().value( null ).build() );
    }

    @Test
    void testCannotBeBlank()
    {
        assertThrows( IllegalArgumentException.class, () -> ScheduledJobName.from( "" ) );
        assertThrows( IllegalArgumentException.class, () -> ScheduledJobName.create().value( "" ).build() );
    }

    @Test
    void testValue()
    {
        assertEquals( "scheduledJobName", ScheduledJobName.from( "scheduledJobName" ).getValue() );
        assertEquals( "scheduledJobName", ScheduledJobName.create().value( "scheduledJobName" ).build().getValue() );
    }

    @Test
    void testToString()
    {
        assertEquals( "scheduledJobName", ScheduledJobName.from( "scheduledJobName" ).toString() );
        assertEquals( "scheduledJobName", ScheduledJobName.create().value( "scheduledJobName" ).build().toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ScheduledJobName.class ).withNonnullFields( "value" ).verify();
    }
}

