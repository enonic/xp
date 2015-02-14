package com.enonic.xp.util;

import org.junit.Test;

import junit.framework.Assert;

import com.enonic.xp.util.GeoPoint;

public class GeoPointTest
{
    @Test
    public void testFrom_valid()
    {
        final GeoPoint point = GeoPoint.from( "1.1,-2.2" );
        Assert.assertNotNull( point );
        Assert.assertEquals( "1.1,-2.2", point.toString() );
        Assert.assertEquals( 1.1, point.getLatitude() );
        Assert.assertEquals( -2.2, point.getLongitude() );
    }

    @Test
    public void testEquals()
    {
        final GeoPoint p1 = new GeoPoint( 1.1, 2.2 );
        final GeoPoint p2 = new GeoPoint( 1.1, 2.2 );
        final GeoPoint p3 = new GeoPoint( 2.2, 1.1 );

        Assert.assertTrue( p1.equals( p2 ) );
        Assert.assertTrue( p2.equals( p1 ) );
        Assert.assertFalse( p3.equals( p1 ) );
        Assert.assertFalse( p2.equals( p3 ) );
    }

    @Test
    public void testHashCode()
    {
        final GeoPoint p1 = new GeoPoint( 1.1, 2.2 );
        final GeoPoint p2 = new GeoPoint( 1.1, 2.2 );
        final GeoPoint p3 = new GeoPoint( 2.2, 1.1 );

        Assert.assertTrue( p1.hashCode() == p2.hashCode() );
        Assert.assertTrue( p3.hashCode() != p1.hashCode() );
    }

    @Test
    public void testFrom_notValid()
    {
        testNotValidFrom( "1", "Value [1] is not a valid geo-point" );
        testNotValidFrom( "1,2,3", "Value [1,2,3] is not a valid geo-point" );
        testNotValidFrom( "a,1", "Value [a,1] is not a valid geo-point" );
        testNotValidFrom( "-90.1,-180", "Latitude [-90.1] is not within range [-90.0‥90.0]" );
        testNotValidFrom( "-90,-180.1", "Longitude [-180.1] is not within range [-180.0‥180.0]" );
        testNotValidFrom( "90.1,180", "Latitude [90.1] is not within range [-90.0‥90.0]" );
        testNotValidFrom( "90,180.1", "Longitude [180.1] is not within range [-180.0‥180.0]" );
    }

    private void testNotValidFrom( final String value, final String message )
    {
        try
        {
            GeoPoint.from( value );
            Assert.fail( "Should throw IllegalArgumentException exception" );
        }
        catch ( final Exception e )
        {
            Assert.assertEquals( IllegalArgumentException.class, e.getClass() );
            Assert.assertEquals( message, e.getMessage() );
        }
    }
}

