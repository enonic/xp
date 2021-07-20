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
    private static CronCalendarImpl deserialize( byte[] bytes )
        throws IOException, ClassNotFoundException
    {
        try (ByteArrayInputStream bais = new ByteArrayInputStream( bytes ); ObjectInputStream ois = new ObjectInputStream( bais ))
        {
            return (CronCalendarImpl) ois.readObject();
        }
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
    public void createWrongValue()
    {
        assertThrows( IllegalArgumentException.class, () -> CronCalendarImpl.create().
            value( "wrong value" ).
            timeZone( TimeZone.getDefault() ).
            build() );
    }

    @Test
    public void testIsCronValue()
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
    public void create()
    {
        final CronCalendarImpl calendar = CronCalendarImpl.create().
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

        final CronCalendarImpl calendar = CronCalendarImpl.create().
            value( "* * * * *" ).
            timeZone( TimeZone.getTimeZone( "GMT+5:30" ) ).
            build();

        byte[] serialized = serialize( calendar );

        final CronCalendarImpl deserializedCalendar = deserialize( serialized );

        assertEquals( calendar.getCronValue(), deserializedCalendar.getCronValue() );
        assertEquals( calendar.getTimeZone(), deserializedCalendar.getTimeZone() );
    }
}