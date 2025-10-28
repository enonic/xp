package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class GeoPointTest
{
    @Test
    void testFrom_valid()
    {
        final GeoPoint point = GeoPoint.from( "1.1,-2.2" );
        assertNotNull( point );
        assertEquals( "1.1,-2.2", point.toString() );
        assertEquals( 1.1, point.getLatitude(), 0 );
        assertEquals( -2.2, point.getLongitude(), 0 );
    }

    @Test
    void testEquals()
    {
        final GeoPoint p1 = new GeoPoint( 1.1, 2.2 );
        final GeoPoint p2 = new GeoPoint( 1.1, 2.2 );
        final GeoPoint p3 = new GeoPoint( 2.2, 1.1 );

        assertTrue( p1.equals( p2 ) );
        assertTrue( p2.equals( p1 ) );
        assertFalse( p3.equals( p1 ) );
        assertFalse( p2.equals( p3 ) );
    }

    @Test
    void testHashCode()
    {
        final GeoPoint p1 = new GeoPoint( 1.1, 2.2 );
        final GeoPoint p2 = new GeoPoint( 1.1, 2.2 );
        final GeoPoint p3 = new GeoPoint( 2.2, 1.1 );

        assertTrue( p1.hashCode() == p2.hashCode() );
        assertTrue( p3.hashCode() != p1.hashCode() );
    }

    @Test
    void testFrom_notValid()
    {
        testNotValidFrom( "1", "Value [1] is not a valid geo-point" );
        testNotValidFrom( "1,2,3", "Value [1,2,3] is not a valid geo-point" );
        testNotValidFrom( "a,1", "Value [a,1] is not a valid geo-point" );
        testNotValidFrom( "-90.1,-180", "Latitude [-90.1] is not within range [-90.0..90.0]" );
        testNotValidFrom( "-90,-180.1", "Longitude [-180.1] is not within range [-180.0..180.0]" );
        testNotValidFrom( "90.1,180", "Latitude [90.1] is not within range [-90.0..90.0]" );
        testNotValidFrom( "90,180.1", "Longitude [180.1] is not within range [-180.0..180.0]" );
    }

    private void testNotValidFrom( final String value, final String message )
    {
        try
        {
            GeoPoint.from( value );
            fail( "Should throw IllegalArgumentException exception" );
        }
        catch ( final Exception e )
        {
            assertEquals( IllegalArgumentException.class, e.getClass() );
            assertEquals( message, e.getMessage() );
        }
    }
}

