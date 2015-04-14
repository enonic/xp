package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.content.ContentId;

import static org.junit.Assert.*;

public class JavaTypeConvertersTest
{
    @Test
    public void isNumber()
    {
        assertTrue( JavaTypeConverters.isNumber( "0" ) );
        assertFalse( JavaTypeConverters.isNumber( "test" ) );
    }

    @Test
    public void convertToDouble()
    {
        assertEquals( new Double( 1.5 ), JavaTypeConverters.DOUBLE.convertFrom( "1.5" ) );
        assertNull( JavaTypeConverters.DOUBLE.convertFrom( "test" ) );
        assertNull( JavaTypeConverters.DOUBLE.convertFrom( new Object() ) );
    }

    @Test
    public void convertToBoolean()
    {
        assertTrue( JavaTypeConverters.BOOLEAN.convertFrom( true ) );
        assertFalse( JavaTypeConverters.BOOLEAN.convertFrom( false ) );
        assertTrue( JavaTypeConverters.BOOLEAN.convertFrom( "true" ) );
        assertFalse( JavaTypeConverters.BOOLEAN.convertFrom( "halftrue" ) );
        assertNull( JavaTypeConverters.BOOLEAN.convertFrom( new Object() ) );
    }

    @Test
    public void convertToLong()
    {
        assertEquals( new Long( 1000 ), JavaTypeConverters.LONG.convertFrom( 1000 ) );
        assertEquals( new Long( 1001 ), JavaTypeConverters.LONG.convertFrom( "1001" ) );
        assertNull( JavaTypeConverters.LONG.convertFrom( "1001test" ) );
        assertNull( JavaTypeConverters.LONG.convertFrom( new Object() ) );
    }

    @Test
    public void convertToString()
    {
        assertNotNull( JavaTypeConverters.STRING.convertFrom( "test convert" ) );
        assertEquals( "converting", JavaTypeConverters.STRING.convertFrom( "converting" ) );
        assertNotNull( JavaTypeConverters.STRING.convertFrom( LocalDateTime.now() ) );
        assertNull( JavaTypeConverters.STRING.convertFrom( new PropertySet() ) );
    }

    @Test
    public void convertToData()
    {
        assertNull( JavaTypeConverters.DATA.convertFrom( "test convert" ) );
        assertNull( JavaTypeConverters.DATA.convertFrom( new Object() ) );
    }

    @Test
    public void convertToContentId()
    {
        ContentId contentId = ContentId.from( "id" );
        assertEquals( contentId, JavaTypeConverters.CONTENT_ID.convertFrom( contentId ) );
        assertEquals( contentId, JavaTypeConverters.CONTENT_ID.convertFrom( "id" ) );
        assertNull( JavaTypeConverters.CONTENT_ID.convertFrom( new Object() ) );
    }

    @Test
    @Ignore
    public void convertToInstant()
    {
        assertEquals( Instant.class, JavaTypeConverters.DATE_TIME.convertFrom( LocalDate.now() ).getClass() );
        assertEquals( Instant.class, JavaTypeConverters.DATE_TIME.convertFrom( LocalTime.now() ).getClass() );
        assertEquals( Instant.class, JavaTypeConverters.DATE_TIME.convertFrom( LocalDateTime.now() ).getClass() );
        assertEquals( Instant.class, JavaTypeConverters.DATE_TIME.convertFrom( Instant.now() ).getClass() );
        assertNull( JavaTypeConverters.DATE_TIME.convertFrom( new Object() ) );
    }

    @Test
    public void convertToLocalTime()
    {
        assertEquals( LocalTime.class, JavaTypeConverters.LOCAL_TIME.convertFrom( LocalDate.now() ).getClass() );
        assertEquals( LocalTime.class, JavaTypeConverters.LOCAL_TIME.convertFrom( LocalTime.now() ).getClass() );
        assertEquals( LocalTime.class, JavaTypeConverters.LOCAL_TIME.convertFrom( LocalDateTime.now() ).getClass() );
        assertEquals( LocalTime.class, JavaTypeConverters.LOCAL_TIME.convertFrom( Instant.now() ).getClass() );
        assertNull( JavaTypeConverters.LOCAL_TIME.convertFrom( new Object() ) );
    }


    @Test
    public void convertToLocalDateTime()
    {
        assertEquals( LocalDateTime.class, JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( LocalDate.now() ).getClass() );
        assertEquals( LocalDateTime.class, JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( LocalTime.now() ).getClass() );
        assertEquals( LocalDateTime.class, JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( LocalDateTime.now() ).getClass() );
        assertEquals( LocalDateTime.class, JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( Instant.now() ).getClass() );
        assertNull( JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( new Object() ) );
    }

    @Test
    public void convertToLocalDate()
    {
        assertEquals( LocalDate.class, JavaTypeConverters.LOCAL_DATE.convertFrom( LocalDate.now() ).getClass() );
        assertEquals( LocalDate.class, JavaTypeConverters.LOCAL_DATE.convertFrom( LocalDateTime.now() ).getClass() );
        assertEquals( LocalDate.class, JavaTypeConverters.LOCAL_DATE.convertFrom( Instant.now() ).getClass() );
        assertNull( JavaTypeConverters.LOCAL_DATE.convertFrom( new Object() ) );
    }
}
