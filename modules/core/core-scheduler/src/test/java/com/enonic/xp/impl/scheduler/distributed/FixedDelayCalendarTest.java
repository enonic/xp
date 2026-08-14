package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.scheduler.ScheduleCalendarType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixedDelayCalendarTest
{
    @Test
    void nextExecution()
    {
        final FixedDelayCalendarImpl calendar = FixedDelayCalendarImpl.create().duration( Duration.ofMinutes( 5 ) ).build();

        final Instant now = Instant.parse( "2026-01-01T10:30:00Z" );

        assertEquals( now.plus( Duration.ofMinutes( 5 ) ), calendar.nextExecution( now ).orElseThrow() );
        assertEquals( Duration.ofMinutes( 5 ), calendar.getDuration() );
        assertEquals( ScheduleCalendarType.FIXED_DELAY, calendar.getType() );
    }

    @Test
    void invalidDuration()
    {
        assertThrows( NullPointerException.class, () -> FixedDelayCalendarImpl.create().build() );
        assertThrows( IllegalArgumentException.class, () -> FixedDelayCalendarImpl.create().duration( Duration.ZERO ).build() );
        assertThrows( IllegalArgumentException.class,
                      () -> FixedDelayCalendarImpl.create().duration( Duration.ofSeconds( -5 ) ).build() );
    }
}
