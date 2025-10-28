package com.enonic.xp.portal.url;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ImageUrlParamsTest
{
    @Test
    void testId()
    {
        final ImageUrlParams params = new ImageUrlParams();
        assertNull( params.getId() );

        params.id( "" );
        assertNull( params.getId() );

        params.id( "123456" );
        assertEquals( "123456", params.getId() );
    }

    @Test
    void testPath()
    {
        final ImageUrlParams params = new ImageUrlParams();
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    void testFormat()
    {
        final ImageUrlParams params = new ImageUrlParams();
        assertNull( params.getFormat() );

        params.format( "" );
        assertNull( params.getFormat() );

        params.format( "png" );
        assertEquals( "png", params.getFormat() );
    }

    @Test
    void testFilter()
    {
        final ImageUrlParams params = new ImageUrlParams();
        assertNull( params.getFilter() );

        params.filter( "" );
        assertNull( params.getFilter() );

        params.filter( "scale(10,10)" );
        assertEquals( "scale(10,10)", params.getFilter() );
    }

    @Test
    void testBackground()
    {
        final ImageUrlParams params = new ImageUrlParams();
        assertNull( params.getBackground() );

        params.background( "" );
        assertNull( params.getBackground() );

        params.background( "00ff00" );
        assertEquals( "00ff00", params.getBackground() );
    }

    @Test
    void testQuality()
    {
        final ImageUrlParams params = new ImageUrlParams();
        assertNull( params.getQuality() );

        params.quality( "" );
        assertNull( params.getQuality() );

        params.quality( "90" );
        assertEquals( (Integer) 90, params.getQuality() );
    }

    @Test
    void testSetAsMap()
    {
        final ImageUrlParams params = new ImageUrlParams();
        params.id( "123456" );
        params.path( "/a/b" );
        params.format( "png" );
        params.background( "00ff00" );
        params.quality( 90 );
        params.filter( "scale(10,10)" );
        params.param( "a", "1" );

        assertEquals( "123456", params.getId() );
        assertEquals( "/a/b", params.getPath() );
        assertEquals( "png", params.getFormat() );
        assertEquals( "00ff00", params.getBackground() );
        assertEquals( (Integer) 90, params.getQuality() );
        assertEquals( "scale(10,10)", params.getFilter() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals(
            "ImageUrlParams{type=server, params={a=[1]}, id=123456, path=/a/b, format=png, quality=90, filter=scale(10,10), background=00ff00}",
            params.toString() );
    }
}
