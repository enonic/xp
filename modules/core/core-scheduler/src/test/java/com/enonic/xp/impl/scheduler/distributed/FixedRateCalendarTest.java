package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.scheduler.ScheduleCalendarType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FixedRateCalendarTest
{
    @Test
    void nextExecution()
    {
        final FixedRateCalendarImpl calendar = FixedRateCalendarImpl.create().duration( Duration.ofMinutes( 5 ) ).build();

        final Instant now = Instant.parse( "2026-01-01T10:30:00Z" );

        assertEquals( now.plus( Duration.ofMinutes( 5 ) ), calendar.nextExecution( now ).orElseThrow() );
        assertEquals( Duration.ofMinutes( 5 ), calendar.getDuration() );
        assertEquals( ScheduleCalendarType.FIXED_RATE, calendar.getType() );
    }

    @Test
    void invalidDuration()
    {
        assertThrows( NullPointerException.class, () -> FixedRateCalendarImpl.create().build() );
        assertThrows( IllegalArgumentException.class, () -> FixedRateCalendarImpl.create().duration( Duration.ZERO ).build() );
        assertThrows( IllegalArgumentException.class,
                      () -> FixedRateCalendarImpl.create().duration( Duration.ofSeconds( -5 ) ).build() );
    }
}
