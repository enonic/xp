package com.enonic.wem.core.servlet;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ServletRequestUrlHelperTest
{
    private HttpServletRequest request;

    @Before
    public void setup()
    {
        this.request = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( this.request );
    }

    @Test
    public void createUrl_http_port80()
    {
        setupRequest( "http", "localhost", 80, null );

        final String url1 = ServletRequestUrlHelper.createUrl( null );
        assertEquals( "http://localhost", url1 );

        final String url2 = ServletRequestUrlHelper.createUrl( "a/b" );
        assertEquals( "http://localhost/a/b", url2 );

        final String url3 = ServletRequestUrlHelper.createUrl( "/a/b" );
        assertEquals( "http://localhost/a/b", url3 );
    }

    @Test
    public void createUrl_https_port443()
    {
        setupRequest( "https", "localhost", 443, null );

        final String url = ServletRequestUrlHelper.createUrl( "/a/b" );
        assertEquals( "https://localhost/a/b", url );
    }

    @Test
    public void createUrl_http_port8080()
    {
        setupRequest( "http", "localhost", 8080, null );

        final String url = ServletRequestUrlHelper.createUrl( "/a/b" );
        assertEquals( "http://localhost:8080/a/b", url );
    }

    @Test
    public void createUrl_https_port8888()
    {
        setupRequest( "https", "localhost", 8888, null );

        final String url = ServletRequestUrlHelper.createUrl( "/a/b" );
        assertEquals( "https://localhost:8888/a/b", url );
    }

    @Test
    public void createUrl_with_contextPath()
    {
        setupRequest( "http", "localhost", 80, "/context/path" );

        final String url = ServletRequestUrlHelper.createUrl( "/a/b" );
        assertEquals( "http://localhost/context/path/a/b", url );
    }

    private void setupRequest( final String scheme, final String host, final int port, final String contextPath )
    {
        Mockito.when( this.request.getScheme() ).thenReturn( scheme );
        Mockito.when( this.request.getServerName() ).thenReturn( host );
        Mockito.when( this.request.getLocalPort() ).thenReturn( port );
        Mockito.when( this.request.getContextPath() ).thenReturn( contextPath );
    }
}
