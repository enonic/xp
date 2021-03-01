package com.enonic.xp.impl.scheduler.distributed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.scheduler.ScheduleCalendarType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CronCalendarTest
{
    @Test
    public void createWrongValue()
    {
        assertThrows( IllegalArgumentException.class, () -> CronCalendar.create().
            value( "wrong value" ).
            timeZone( TimeZone.getDefault() ).
            build() );
    }

    private static byte[] serialize( Serializable serializable )
        throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream( baos ))
        {
            oos.writeObject( serializable );
            return baos.toByteArray();
        }
    }

    @Test
    public void testIsCronValue()
    {
        assertTrue( CronCalendar.isCronValue( "* * * * *" ) );
        assertTrue( CronCalendar.isCronValue( "1-59/2 * * * *" ) );
        assertTrue( CronCalendar.isCronValue( "0 9-17 * * *" ) );
        assertTrue( CronCalendar.isCronValue( "0 0 * * 6,0" ) );
        assertTrue( CronCalendar.isCronValue( "0 0 1 */6 *" ) );

        assertFalse( CronCalendar.isCronValue( "wrong value" ) );
        assertFalse( CronCalendar.isCronValue( "* * * * * *" ) );
        assertFalse( CronCalendar.isCronValue( "* * * * 8" ) );
    }

    @Test
    public void testDescription()
    {
        assertEquals( "every minute",
                      CronCalendar.create().value( "* * * * *" ).timeZone( TimeZone.getDefault() ).build().getDescription() );
        assertEquals( "every 2 minutes between 1 and 59",
                      CronCalendar.create().value( "1-59/2 * * * *" ).timeZone( TimeZone.getDefault() ).build().getDescription() );
        assertEquals( "every hour between 9 and 17",
                      CronCalendar.create().value( "0 9-17 * * *" ).timeZone( TimeZone.getDefault() ).build().getDescription() );
        assertEquals( "at 00:00 at Saturday and Sunday days",
                      CronCalendar.create().value( "0 0 * * 6,0" ).timeZone( TimeZone.getDefault() ).build().getDescription() );
        assertEquals( "at 00:00 at 1 day every 6 months",
                      CronCalendar.create().value( "0 0 1 */6 *" ).timeZone( TimeZone.getDefault() ).build().getDescription() );
    }

    private static CronCalendar deserialize( byte[] bytes )
        throws IOException, ClassNotFoundException
    {
        try (ByteArrayInputStream bais = new ByteArrayInputStream( bytes ); ObjectInputStream ois = new ObjectInputStream( bais ))
        {
            return (CronCalendar) ois.readObject();
        }
    }

    @Test
    public void create()
    {
        final CronCalendar calendar = CronCalendar.create().
            value( "* * * * *" ).
            timeZone( TimeZone.getDefault() ).
            build();

        assertTrue( calendar.nextExecution().get().toSeconds() <= 60 );
        assertEquals( ScheduleCalendarType.CRON, calendar.getType() );
    }

    @Test
    public void calendarSerialized()
        throws Exception
    {

        final CronCalendar calendar = CronCalendar.create().
            value( "* * * * *" ).
            timeZone( TimeZone.getTimeZone( "GMT+5:30" ) ).
            build();

        byte[] serialized = serialize( calendar );

        final CronCalendar deserializedCalendar = deserialize( serialized );

        assertEquals( calendar.getCronValue(), deserializedCalendar.getCronValue() );
        assertEquals( calendar.getDescription(), deserializedCalendar.getDescription() );
        assertEquals( calendar.getTimeZone(), deserializedCalendar.getTimeZone() );
    }
}