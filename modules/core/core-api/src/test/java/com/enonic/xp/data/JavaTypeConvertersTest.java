package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaTypeConvertersTest
{
    @Test
    void isNumber()
    {
        assertTrue( JavaTypeConverters.isNumber( "0" ) );
        assertFalse( JavaTypeConverters.isNumber( "test" ) );
    }

    @Test
    void convertToDouble()
    {
        assertEquals( 1.5, JavaTypeConverters.DOUBLE.convertFrom( "1.5" ) );
        assertNull( JavaTypeConverters.DOUBLE.convertFrom( "test" ) );
        assertNull( JavaTypeConverters.DOUBLE.convertFrom( new Object() ) );
    }

    @Test
    void convertToBoolean()
    {
        assertTrue( JavaTypeConverters.BOOLEAN.convertFrom( true ) );
        assertFalse( JavaTypeConverters.BOOLEAN.convertFrom( false ) );
        assertTrue( JavaTypeConverters.BOOLEAN.convertFrom( "true" ) );
        assertFalse( JavaTypeConverters.BOOLEAN.convertFrom( "halftrue" ) );
        assertNull( JavaTypeConverters.BOOLEAN.convertFrom( new Object() ) );
    }

    @Test
    void convertToLong()
    {
        assertEquals( 1000, JavaTypeConverters.LONG.convertFrom( 1000 ) );
        assertEquals( 1001, JavaTypeConverters.LONG.convertFrom( "1001" ) );
        assertEquals( 1001, JavaTypeConverters.LONG.convertFrom( "1001.0" ) );
        assertNull( JavaTypeConverters.LONG.convertFrom( "1001test" ) );
        assertNull( JavaTypeConverters.LONG.convertFrom( new Object() ) );
    }

    @Test
    void convertToString()
    {
        assertNotNull( JavaTypeConverters.STRING.convertFrom( "test convert" ) );
        assertEquals( "converting", JavaTypeConverters.STRING.convertFrom( "converting" ) );
        assertNotNull( JavaTypeConverters.STRING.convertFrom( LocalDateTime.now() ) );
        assertNotNull( JavaTypeConverters.STRING.convertFrom( new PropertyTree().newSet() ) );
        assertEquals( "\n", JavaTypeConverters.STRING.convertFrom( new PropertyTree().newSet() ) );
    }

    @Test
    void convertToData()
    {
        assertNull( JavaTypeConverters.DATA.convertFrom( "test convert" ) );
        assertNull( JavaTypeConverters.DATA.convertFrom( new Object() ) );
    }

    @Test
    void convertToContentId()
    {
        ContentId contentId = ContentId.from( "id" );
        assertEquals( contentId, JavaTypeConverters.CONTENT_ID.convertFrom( contentId ) );
        assertEquals( contentId, JavaTypeConverters.CONTENT_ID.convertFrom( "id" ) );
        assertNull( JavaTypeConverters.CONTENT_ID.convertFrom( new Object() ) );
    }

    @Test
    void convertToInstant()
    {
        assertEquals( Instant.class, JavaTypeConverters.DATE_TIME.convertFrom( LocalDate.now() ).getClass() );
        assertEquals( Instant.class, JavaTypeConverters.DATE_TIME.convertFrom( LocalTime.now() ).getClass() );
        assertEquals( Instant.class, JavaTypeConverters.DATE_TIME.convertFrom( LocalDateTime.now() ).getClass() );
        assertEquals( Instant.class, JavaTypeConverters.DATE_TIME.convertFrom( Instant.now() ).getClass() );
        assertNull( JavaTypeConverters.DATE_TIME.convertFrom( new Object() ) );
    }

    @Test
    void convertToLocalTime()
    {
        assertEquals( LocalTime.class, JavaTypeConverters.LOCAL_TIME.convertFrom( LocalDate.now() ).getClass() );
        assertEquals( LocalTime.class, JavaTypeConverters.LOCAL_TIME.convertFrom( LocalTime.now() ).getClass() );
        assertEquals( LocalTime.class, JavaTypeConverters.LOCAL_TIME.convertFrom( LocalDateTime.now() ).getClass() );
        assertEquals( LocalTime.class, JavaTypeConverters.LOCAL_TIME.convertFrom( Instant.now() ).getClass() );
        assertNull( JavaTypeConverters.LOCAL_TIME.convertFrom( new Object() ) );
    }


    @Test
    void convertToLocalDateTime()
    {
        assertEquals( LocalDateTime.class, JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( LocalDate.now() ).getClass() );
        assertEquals( LocalDateTime.class, JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( LocalTime.now() ).getClass() );
        assertEquals( LocalDateTime.class, JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( LocalDateTime.now() ).getClass() );
        assertEquals( LocalDateTime.class, JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( Instant.now() ).getClass() );
        assertNull( JavaTypeConverters.LOCAL_DATE_TIME.convertFrom( new Object() ) );
    }

    @Test
    void convertToLocalDate()
    {
        assertEquals( LocalDate.class, JavaTypeConverters.LOCAL_DATE.convertFrom( LocalDate.now() ).getClass() );
        assertEquals( LocalDate.class, JavaTypeConverters.LOCAL_DATE.convertFrom( LocalDateTime.now() ).getClass() );
        assertEquals( LocalDate.class, JavaTypeConverters.LOCAL_DATE.convertFrom( Instant.now() ).getClass() );
        assertNull( JavaTypeConverters.LOCAL_DATE.convertFrom( new Object() ) );
    }

    @Test
    void convertToGeoPoint()
    {
        assertEquals( GeoPoint.class, JavaTypeConverters.GEO_POINT.convertFrom( GeoPoint.from( "22.22, 33.33" ) ).getClass() );
        assertEquals( GeoPoint.class, JavaTypeConverters.GEO_POINT.convertFrom( new GeoPoint( 2.2, 3.3 ) ).getClass() );
        assertEquals( GeoPoint.class, JavaTypeConverters.GEO_POINT.convertFrom( "22.22, 33.33" ).getClass() );

        final PropertySet set = new PropertyTree().newSet();
        set.addDouble( "lat", 2.2 );
        set.addDouble( "lon", 3.3 );
        assertEquals( GeoPoint.class, JavaTypeConverters.GEO_POINT.convertFrom( set ).getClass() );

        assertNull( JavaTypeConverters.GEO_POINT.convertFrom( new Object() ) );
        assertNull( JavaTypeConverters.GEO_POINT.convertFrom( null ) );
    }
}
