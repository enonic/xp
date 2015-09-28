package com.enonic.xp.web;

import java.net.URI;
import java.time.Instant;
import java.util.EnumSet;

import org.junit.Test;

import com.google.common.collect.Multimap;
import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class HttpHeadersTest
{
    @Test
    public void testFirst()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set( "TEST", "a1" );
        httpHeaders.set( "TEST", "a2" );

        assertEquals( httpHeaders.getFirst( "TEST" ), "a1" );
        assertEquals( httpHeaders.getFirst( "TEST2" ), null );
    }

    @Test
    public void testSetContentTypeNull()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( null );

        assertEquals( httpHeaders.getContentType(), null );
    }

    @Test
    public void testSetDateNull()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setDate( null );

        assertEquals( httpHeaders.getDate(), null );
    }

    @Test
    public void testSetExpiresNull()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setExpires( null );

        assertEquals( httpHeaders.getExpires(), null );
    }

    @Test
    public void testSetUriNull()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation( null );

        assertEquals( httpHeaders.getLocation(), null );
    }

    @Test
    public void testSetRefererNull()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setReferer( null );

        assertEquals( httpHeaders.getReferer(), null );
    }

    @Test
    public void testSetLastModifiedNull()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLastModified( null );

        assertEquals( httpHeaders.getLastModified(), null );
    }

    @Test
    public void testSetAllowNull()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAllow();

        assertEquals( httpHeaders.getAllow(), EnumSet.noneOf( HttpMethod.class ) );
    }

    @Test
    public void testAllow()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAllow( HttpMethod.GET, HttpMethod.POST );

        assertTrue( httpHeaders.getAllow().contains( HttpMethod.GET ) );
        assertTrue( httpHeaders.getAllow().contains( HttpMethod.POST ) );
        assertFalse( httpHeaders.getAllow().contains( HttpMethod.DELETE ) );
    }

    @Test
    public void testAllowEmpty()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        assertEquals( httpHeaders.getAllow(), EnumSet.noneOf( HttpMethod.class ) );

        httpHeaders.setAllow();
        assertEquals( httpHeaders.getAllow(), EnumSet.noneOf( HttpMethod.class ) );
    }

    @Test
    public void testContentType()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.ANY_TYPE );

        assertEquals( httpHeaders.getContentType(), MediaType.ANY_TYPE );
    }

    @Test
    public void testContentLength()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength( 100L );

        assertEquals( httpHeaders.getContentLength(), 100L );
    }

    @Test
    public void testContentLengthEmpty()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        assertEquals( httpHeaders.getContentLength(), -1 );
    }

    @Test
    public void testDate()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        Instant now = Instant.now();
        httpHeaders.setDate( now );

        assertEquals( httpHeaders.getDate(), now );
    }

    @Test
    public void testDateEmpty()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        assertEquals( httpHeaders.getDate(), null );
    }

    @Test
    public void testLastModified()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        Instant now = Instant.now();
        httpHeaders.setLastModified( now );

        assertEquals( httpHeaders.getLastModified(), now );
    }

    @Test
    public void testExpires()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        Instant now = Instant.now();
        httpHeaders.setExpires( now );

        assertEquals( httpHeaders.getExpires(), now );
    }

    @Test
    public void testLocation()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        URI location = URI.create( "www.enonic.com/test" );
        httpHeaders.setLocation( location );

        assertEquals( httpHeaders.getLocation(), location );
    }

    @Test
    public void testLocationEmpty()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();

        assertEquals( httpHeaders.getLocation(), null );
    }

    @Test
    public void testReferer()
    {
        final HttpHeaders httpHeaders = new HttpHeaders();
        URI referer = URI.create( "www.enonic.com/test/referer" );
        httpHeaders.setReferer( referer );

        assertEquals( httpHeaders.getReferer(), referer );
    }

    @Test
    public void testAsMap()
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.set( "TEST", "a1" );
        headers.set( "TEST", "a2" );

        final Multimap<String, String> map = headers.getAsMap();
        assertNotNull( map );
        assertEquals( 1, map.keySet().size() );
        assertEquals( 2, map.size() );
    }
}
