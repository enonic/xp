package com.enonic.xp.impl.scheduler.distributed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.scheduler.ScheduleCalendarType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class OneTimeCalendarTest
{
    private static byte[] serialize( Serializable serializable )
        throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream( baos ))
        {
            oos.writeObject( serializable );
            return baos.toByteArray();
        }
    }

    private static OneTimeCalendar deserialize( byte[] bytes )
        throws IOException, ClassNotFoundException
    {
        try (ByteArrayInputStream bais = new ByteArrayInputStream( bytes ); ObjectInputStream ois = new ObjectInputStream( bais ))
        {
            return (OneTimeCalendar) ois.readObject();
        }
    }

    @Test
    public void createWrongValue()
    {
        assertThrows( NullPointerException.class, () -> OneTimeCalendar.create().
            value( null ).
            build() );
    }

    @Test
    public void create()
    {
        OneTimeCalendar calendar = OneTimeCalendar.create().
            value( Instant.now().plus( Duration.of( 1, ChronoUnit.MINUTES ) ) ).
            build();

        assertFalse( calendar.nextExecution().get().isNegative() );
        assertEquals( ScheduleCalendarType.ONE_TIME, calendar.getType() );

        calendar = OneTimeCalendar.create().
            value( Instant.now().minus( Duration.of( 1, ChronoUnit.SECONDS ) ) ).
            build();

        assertTrue( calendar.nextExecution().get().isNegative() );
    }

    @Test
    public void calendarSerialized()
        throws Exception
    {

        final OneTimeCalendar calendar = OneTimeCalendar.create().
            value( Instant.now() ).
            build();

        byte[] serialized = serialize( calendar );

        final OneTimeCalendar deserializedCalendar = deserialize( serialized );

        assertEquals( calendar.getValue(), deserializedCalendar.getValue() );
    }
}