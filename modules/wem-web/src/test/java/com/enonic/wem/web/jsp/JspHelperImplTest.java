package com.enonic.wem.web.jsp;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

import com.enonic.cms.core.product.ProductVersion;

public class JspHelperImplTest
{
    private JspHelperImpl helper;

    @Before
    public void setUp()
    {
        this.helper = new JspHelperImpl();
    }

    @Test
    public void testGetProductVersion()
    {
        final String expected = ProductVersion.getFullTitleAndVersion();
        final String str = this.helper.getProductVersion();
        assertEquals( expected, str );
    }

    @Test
    public void testEllipsis()
    {
        final String str1 = this.helper.ellipsis( "", 10 );
        assertEquals( "", str1 );

        final String str2 = this.helper.ellipsis( "1234567890", 10 );
        assertEquals( "1234567890", str2 );

        final String str3 = this.helper.ellipsis( "123456789012", 10 );
        assertEquals( "1234567890...", str3 );
    }

    @Test
    public void testGetBaseUrl()
    {
        final MockHttpServletRequest req = new MockHttpServletRequest();
        this.helper.setServletRequest( req );

        req.setRequestURI( "/" );
        final String url1 = this.helper.getBaseUrl();
        assertEquals( "http://localhost", url1 );

        req.setRequestURI( "/test" );
        final String url2 = this.helper.getBaseUrl();
        assertEquals( "http://localhost/test", url2 );

        req.setServerPort( 8888 );
        req.setRequestURI( "/test/" );
        final String url3 = this.helper.getBaseUrl();
        assertEquals( "http://localhost:8888/test", url3 );
    }

    @Test
    public void testCreateUrl()
    {
        final MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI( "/" );
        this.helper.setServletRequest( req );

        final String url1 = this.helper.createUrl( "" );
        assertEquals( "http://localhost", url1 );

        final String url2 = this.helper.createUrl( "hello" );
        assertEquals( "http://localhost/hello", url2 );

        final String url3 = this.helper.createUrl( "hello/world" );
        assertEquals( "http://localhost/hello/world", url3 );
    }
}
