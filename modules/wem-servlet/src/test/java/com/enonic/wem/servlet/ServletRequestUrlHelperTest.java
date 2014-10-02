package com.enonic.wem.servlet;

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
    public void createUri()
    {
        setupRequest( null );

        final String uri1 = ServletRequestUrlHelper.createUri( null );
        assertEquals( "", uri1 );

        final String uri2 = ServletRequestUrlHelper.createUri( "" );
        assertEquals( "", uri2 );

        final String uri3 = ServletRequestUrlHelper.createUri( "a/b" );
        assertEquals( "/a/b", uri3 );

        final String uri4 = ServletRequestUrlHelper.createUri( "/a/b" );
        assertEquals( "/a/b", uri4 );
    }

    @Test
    public void createUri_withContextPath()
    {
        setupRequest( "/context/path" );

        final String uri1 = ServletRequestUrlHelper.createUri( null );
        assertEquals( "/context/path", uri1 );

        final String uri2 = ServletRequestUrlHelper.createUri( "" );
        assertEquals( "/context/path", uri2 );

        final String uri3 = ServletRequestUrlHelper.createUri( "a/b" );
        assertEquals( "/context/path/a/b", uri3 );

        final String uri4 = ServletRequestUrlHelper.createUri( "/a/b" );
        assertEquals( "/context/path/a/b", uri4 );
    }

    private void setupRequest( final String contextPath )
    {
        Mockito.when( this.request.getContextPath() ).thenReturn( contextPath );
    }
}
