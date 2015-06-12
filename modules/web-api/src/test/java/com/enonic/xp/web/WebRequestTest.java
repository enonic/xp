package com.enonic.xp.web;

import java.net.URI;
import java.time.Instant;
import java.util.EnumSet;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.net.HostAndPort;
import com.google.common.net.MediaType;

import static org.junit.Assert.*;

public class WebRequestTest
{

    private HttpServletRequest httpServletRequest;

    private WebRequest webRequest;

    @Before
    public void setUp()
        throws Exception
    {
        this.httpServletRequest = Mockito.mock( HttpServletRequest.class );
        this.webRequest = WebRequest.from( this.httpServletRequest );
    }

    @Test
    public void testHttpMethod()
    {
        Mockito.when( this.httpServletRequest.getMethod() ).thenReturn( "GET" );

        assertEquals( this.webRequest.getMethod(), HttpMethod.GET );
    }

    @Test
    public void testGetPath()
    {
        Mockito.when( this.httpServletRequest.getPathInfo() ).thenReturn( "pathInfo" );

        assertEquals( this.webRequest.getPath(), "pathInfo" );
    }

    @Test
    public void testGetRawRequest()
    {
        assertNotNull( this.webRequest.getRawRequest() );
        assertEquals( this.webRequest.getRawRequest(), this.httpServletRequest );
    }

    @Test
    public void testGetLocalAddress()
    {
        Mockito.when( this.httpServletRequest.getLocalName() ).thenReturn( "localhost" );
        Mockito.when( this.httpServletRequest.getLocalPort() ).thenReturn( 80 );

        assertEquals( this.webRequest.getLocalAddress(), HostAndPort.fromParts( "localhost", 80 ) );
    }

    @Test
    public void testGetRemoteAddress()
    {
        Mockito.when( this.httpServletRequest.getRemoteHost() ).thenReturn( "localhost" );
        Mockito.when( this.httpServletRequest.getRemotePort() ).thenReturn( 80 );

        assertEquals( this.webRequest.getRemoteAddress(), HostAndPort.fromParts( "localhost", 80 ) );
    }

    @Test
    public void testAllowEmpty()
    {

        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.ALLOW ) ).thenReturn( "" );

        final HttpHeaders httpHeaders = this.webRequest.getHeaders();

        assertEquals( httpHeaders.getAllow(), EnumSet.noneOf( HttpMethod.class ) );
    }

    @Test
    public void testHttpHeaders()
    {
        final Instant now = Instant.now();
        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.DATE ) ).thenReturn( now.toString() );
        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.LAST_MODIFIED ) ).thenReturn( now.toString() );
        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.EXPIRES ) ).thenReturn( now.toString() );

        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.ALLOW ) ).thenReturn( "GET, POST" );

        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.LOCATION ) ).thenReturn( "http://enonic/location" );
        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.REFERER ) ).thenReturn( "http://enonic/referer" );

        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.CONTENT_LENGTH ) ).thenReturn( "1024" );
        Mockito.when( this.httpServletRequest.getHeader( HttpHeaders.CONTENT_TYPE ) ).thenReturn( "*/*" );

        final HttpHeaders httpHeaders = this.webRequest.getHeaders();
        assertEquals( httpHeaders.getDate(), now );
        assertEquals( httpHeaders.getLastModified(), now );
        assertEquals( httpHeaders.getExpires(), now );

        assertTrue( httpHeaders.getAllow().contains( HttpMethod.GET ) );
        assertTrue( httpHeaders.getAllow().contains( HttpMethod.POST ) );

        assertEquals( httpHeaders.getReferer(), URI.create( "http://enonic/referer" ) );
        assertEquals( httpHeaders.getLocation(), URI.create( "http://enonic/location" ) );

        assertEquals( httpHeaders.getContentLength(), 1024 );
        assertEquals( httpHeaders.getContentType(), MediaType.parse( "*/*" ) );
    }
}
