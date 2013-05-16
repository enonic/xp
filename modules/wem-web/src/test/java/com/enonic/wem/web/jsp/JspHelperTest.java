package com.enonic.wem.web.jsp;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Version;

import static org.junit.Assert.*;

public class JspHelperTest
{
    @Test
    public void testGetProductVersion()
    {
        final String expected = Version.get().getNameAndVersion();
        final String str = JspHelper.getProductVersion();
        assertEquals( expected, str );
    }

    @Test
    public void testEllipsis()
    {
        final String str1 = JspHelper.ellipsis( "", 10 );
        assertEquals( "", str1 );

        final String str2 = JspHelper.ellipsis( "1234567890", 10 );
        assertEquals( "1234567890", str2 );

        final String str3 = JspHelper.ellipsis( "123456789012", 10 );
        assertEquals( "1234567890...", str3 );
    }

    @Test
    public void testGetBaseUrl()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );

        final String url1 = JspHelper.getBaseUrl( req );
        assertEquals( "http://localhost", url1 );

        Mockito.when( req.getLocalPort() ).thenReturn( 8888 );
        Mockito.when( req.getContextPath() ).thenReturn( "/test" );
        final String url2 = JspHelper.getBaseUrl( req );
        assertEquals( "http://localhost:8888/test", url2 );
    }

    @Test
    public void testCreateUrl()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );

        final String url1 = JspHelper.createUrl( req, "" );
        assertEquals( "http://localhost", url1 );

        final String url2 = JspHelper.createUrl( req, "hello" );
        assertEquals( "http://localhost/hello", url2 );

        final String url3 = JspHelper.createUrl( req, "hello/world" );
        assertEquals( "http://localhost/hello/world", url3 );
    }
}
