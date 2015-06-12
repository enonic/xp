package com.enonic.xp.web;

import java.net.URI;
import java.time.Instant;
import java.util.EnumSet;

import org.junit.Test;

import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class HttpHeadersTest
{

    @Test
    public void testFirst()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set( "TEST", "a1" );
        httpHeaders.set( "TEST", "a2" );

        assertEquals( httpHeaders.getFirst( "TEST" ), "a1" );
        assertEquals( httpHeaders.getFirst( "TEST2" ), null );
    }

    @Test
    public void testAllow()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAllow( new HttpMethod[]{HttpMethod.GET, HttpMethod.POST} );

        assertTrue( httpHeaders.getAllow().contains( HttpMethod.GET ) );
        assertTrue( httpHeaders.getAllow().contains( HttpMethod.POST ) );
        assertFalse( httpHeaders.getAllow().contains( HttpMethod.DELETE ) );
    }

    @Test
    public void testAllowEmpty()
    {

        HttpHeaders httpHeaders = new HttpHeaders();

        assertEquals( httpHeaders.getAllow(), EnumSet.noneOf( HttpMethod.class ) );

        httpHeaders.setAllow( new HttpMethod[]{} );

        assertEquals( httpHeaders.getAllow(), EnumSet.noneOf( HttpMethod.class ) );
    }

    @Test
    public void testContentType()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType( MediaType.ANY_TYPE );

        assertEquals( httpHeaders.getContentType(), MediaType.ANY_TYPE );
    }

    @Test
    public void testContentLength()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength( 100L );

        assertEquals( httpHeaders.getContentLength(), 100L );
    }

    @Test
    public void testContentLengthEmpty()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        assertEquals( httpHeaders.getContentLength(), -1 );
    }

    @Test
    public void testDate()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        Instant now = Instant.now();
        httpHeaders.setDate( now );

        assertEquals( httpHeaders.getDate(), now );
    }

    @Test
    public void testDateEmpty()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        assertEquals( httpHeaders.getDate(), null );
    }

    @Test
    public void testLastModified()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        Instant now = Instant.now();
        httpHeaders.setLastModified( now );

        assertEquals( httpHeaders.getLastModified(), now );
    }

    @Test
    public void testExpires()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        Instant now = Instant.now();
        httpHeaders.setExpires( now );

        assertEquals( httpHeaders.getExpires(), now );
    }

    @Test
    public void testLocation()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        URI location = URI.create( "www.enonic.com/test" );
        httpHeaders.setLocation( location );

        assertEquals( httpHeaders.getLocation(), location );
    }

    @Test
    public void testLocationEmpty()
    {
        HttpHeaders httpHeaders = new HttpHeaders();

        assertEquals( httpHeaders.getLocation(), null );
    }

    @Test
    public void testReferer()
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        URI referer = URI.create( "www.enonic.com/test/referer" );
        httpHeaders.setReferer( referer );

        assertEquals( httpHeaders.getReferer(), referer );
    }
}
