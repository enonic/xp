package com.enonic.xp.scheduler;


import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScheduledJobNameTest
{
    @Test
    public void testCannotBeNull()
    {
        assertThrows( NullPointerException.class, () -> ScheduledJobName.from( null ) );
        assertThrows( NullPointerException.class, () -> ScheduledJobName.create().value( null ).build() );
    }

    @Test
    public void testCannotBeBlank()
    {
        assertThrows( IllegalArgumentException.class, () -> ScheduledJobName.from( "" ) );
        assertThrows( IllegalArgumentException.class, () -> ScheduledJobName.create().value( "" ).build() );
    }

    @Test
    public void testValue()
    {
        assertEquals( "scheduledJobName", ScheduledJobName.from( "scheduledJobName" ).getValue() );
        assertEquals( "scheduledJobName", ScheduledJobName.create().value( "scheduledJobName" ).build().getValue() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "scheduledJobName", ScheduledJobName.from( "scheduledJobName" ).toString() );
        assertEquals( "scheduledJobName", ScheduledJobName.create().value( "scheduledJobName" ).build().toString() );
    }

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( ScheduledJobName.class ).withNonnullFields( "value" ).verify();
    }
}

