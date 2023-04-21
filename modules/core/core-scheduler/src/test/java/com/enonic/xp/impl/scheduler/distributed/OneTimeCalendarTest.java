package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.support.SerializableUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class OneTimeCalendarTest
{

    @Test
    public void createWrongValue()
    {
        assertThrows( NullPointerException.class, () -> OneTimeCalendarImpl.create().
            value( null ).
            build() );
    }

    @Test
    public void create()
    {
        OneTimeCalendarImpl calendar = OneTimeCalendarImpl.create().
            value( Instant.now().plus( Duration.of( 1, ChronoUnit.MINUTES ) ) ).
            build();

        assertFalse( calendar.timeToNextExecution().get().isNegative() );
        assertEquals( ScheduleCalendarType.ONE_TIME, calendar.getType() );

        calendar = OneTimeCalendarImpl.create().
            value( Instant.now().minus( Duration.of( 1, ChronoUnit.SECONDS ) ) ).
            build();

        assertTrue( calendar.timeToNextExecution().get().isNegative() );
    }

    @Test
    public void calendarSerialized()
        throws Exception
    {

        final OneTimeCalendarImpl calendar = OneTimeCalendarImpl.create().
            value( Instant.now() ).
            build();

        byte[] serialized = SerializableUtils.serialize( calendar );

        final OneTimeCalendarImpl deserializedCalendar = (OneTimeCalendarImpl) SerializableUtils.deserialize( serialized );

        assertEquals( calendar.getValue(), deserializedCalendar.getValue() );
    }
}
