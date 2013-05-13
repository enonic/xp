package com.enonic.wem.web.jsp;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

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
        final MockHttpServletRequest req = new MockHttpServletRequest();

        req.setRequestURI( "/" );
        final String url1 = JspHelper.getBaseUrl( req );
        assertEquals( "http://localhost", url1 );

        req.setRequestURI( "/test" );
        final String url2 = JspHelper.getBaseUrl( req );
        assertEquals( "http://localhost", url2 );

        req.setServerPort( 8888 );
        req.setContextPath( "/test" );
        req.setRequestURI( "/foo" );
        final String url3 = JspHelper.getBaseUrl( req );
        assertEquals( "http://localhost:8888/test", url3 );
    }

    @Test
    public void testCreateUrl()
    {
        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI( "/" );

        final String url1 = JspHelper.createUrl( req, "" );
        assertEquals( "http://localhost", url1 );

        final String url2 = JspHelper.createUrl( req, "hello" );
        assertEquals( "http://localhost/hello", url2 );

        final String url3 = JspHelper.createUrl( req, "hello/world" );
        assertEquals( "http://localhost/hello/world", url3 );
    }
}
