package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

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
class CronCalendarTest
{

    @Test
    void createWrongValue()
    {
        assertThrows( IllegalArgumentException.class, () -> CronCalendarImpl.create().
            value( "wrong value" ).
            timeZone( TimeZone.getDefault() ).
            build() );
    }

    @Test
    void testIsCronValue()
    {
        assertTrue( CronCalendarImpl.isCronValue( "* * * * *" ) );
        assertTrue( CronCalendarImpl.isCronValue( "1-59/2 * * * *" ) );
        assertTrue( CronCalendarImpl.isCronValue( "0 9-17 * * *" ) );
        assertTrue( CronCalendarImpl.isCronValue( "0 0 * * 6,0" ) );
        assertTrue( CronCalendarImpl.isCronValue( "0 0 1 */6 *" ) );

        assertFalse( CronCalendarImpl.isCronValue( "wrong value" ) );
        assertFalse( CronCalendarImpl.isCronValue( "* * * * * *" ) );
        assertFalse( CronCalendarImpl.isCronValue( "* * * * 8" ) );
    }

    @Test
    void create()
    {
        final CronCalendarImpl calendar = CronCalendarImpl.create().
            value( "* * * * *" ).
            timeZone( TimeZone.getDefault() ).
            build();

        final Instant now = Instant.now();
        assertTrue( Duration.between( now, calendar.nextExecution( now ).get() ).get( ChronoUnit.SECONDS ) <= 60 );
        assertEquals( ScheduleCalendarType.CRON, calendar.getType() );
    }

    @Test
    void calendarSerialized()
    {

        final CronCalendarImpl calendar = CronCalendarImpl.create().
            value( "* * * * *" ).
            timeZone( TimeZone.getTimeZone( "GMT+5:30" ) ).
            build();

        byte[] serialized = SerializableUtils.serialize( calendar );

        final CronCalendarImpl deserializedCalendar = (CronCalendarImpl) SerializableUtils.deserialize( serialized );

        assertEquals( calendar.getCronValue(), deserializedCalendar.getCronValue() );
        assertEquals( calendar.getTimeZone(), deserializedCalendar.getTimeZone() );
    }
}
