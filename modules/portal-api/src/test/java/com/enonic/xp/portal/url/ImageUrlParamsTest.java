package com.enonic.xp.portal.url;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import static org.junit.Assert.*;

public class ImageUrlParamsTest
    extends AbstractUrlParamsTest
{
    @Test
    public void testId()
    {
        final ImageUrlParams params = configure( new ImageUrlParams() );
        assertNull( params.getId() );

        params.id( "" );
        assertNull( params.getId() );

        params.id( "123456" );
        assertEquals( "123456", params.getId() );
    }

    @Test
    public void testPath()
    {
        final ImageUrlParams params = configure( new ImageUrlParams() );
        assertNull( params.getPath() );

        params.path( "" );
        assertNull( params.getPath() );

        params.path( "/a/b" );
        assertEquals( "/a/b", params.getPath() );
    }

    @Test
    public void testFormat()
    {
        final ImageUrlParams params = configure( new ImageUrlParams() );
        assertNull( params.getFormat() );

        params.format( "" );
        assertNull( params.getFormat() );

        params.format( "png" );
        assertEquals( "png", params.getFormat() );
    }

    @Test
    public void testFilter()
    {
        final ImageUrlParams params = configure( new ImageUrlParams() );
        assertNull( params.getFilter() );

        params.filter( "" );
        assertNull( params.getFilter() );

        params.filter( "scale(10,10)" );
        assertEquals( "scale(10,10)", params.getFilter() );
    }

    @Test
    public void testBackground()
    {
        final ImageUrlParams params = configure( new ImageUrlParams() );
        assertNull( params.getBackground() );

        params.background( "" );
        assertNull( params.getBackground() );

        params.background( "00ff00" );
        assertEquals( "00ff00", params.getBackground() );
    }

    @Test
    public void testQuality()
    {
        final ImageUrlParams params = configure( new ImageUrlParams() );
        assertNull( params.getQuality() );

        params.quality( "" );
        assertNull( params.getQuality() );

        params.quality( "90" );
        assertEquals( (Integer) 90, params.getQuality() );
    }

    @Test
    public void testSetAsMap()
    {
        final Multimap<String, String> map = HashMultimap.create();
        map.put( "_id", "123456" );
        map.put( "_path", "/a/b" );
        map.put( "_format", "png" );
        map.put( "_background", "00ff00" );
        map.put( "_quality", "90" );
        map.put( "_filter", "scale(10,10)" );
        map.put( "a", "1" );

        final ImageUrlParams params = configure( new ImageUrlParams() );
        params.setAsMap( map );

        assertEquals( "123456", params.getId() );
        assertEquals( "/a/b", params.getPath() );
        assertEquals( "png", params.getFormat() );
        assertEquals( "00ff00", params.getBackground() );
        assertEquals( (Integer) 90, params.getQuality() );
        assertEquals( "scale(10,10)", params.getFilter() );
        assertEquals( "{a=[1]}", params.getParams().toString() );
        assertEquals( "ImageUrlParams{params={a=[1]}, id=123456, path=/a/b, format=png, quality=90, filter=scale(10,10), background=00ff00}",
                      params.toString() );
    }
}
