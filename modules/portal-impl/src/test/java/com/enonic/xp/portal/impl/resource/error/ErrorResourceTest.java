package com.enonic.xp.portal.impl.resource.error;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.portal.impl.resource.base.BaseResourceTest;

import static org.junit.Assert.*;

public class ErrorResourceTest
    extends BaseResourceTest
{
    @Override
    protected void configure()
        throws Exception
    {
    }

    @Test
    public void error_noMesage()
        throws Exception
    {
        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/error/500" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 500, response.getStatus() );
        assertEquals( "text/html", response.getContentType() );
    }

    @Test
    public void error_withMesage()
        throws Exception
    {
        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/error/404" );
        request.setQueryString( "message=Some%20error%20message" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 404, response.getStatus() );
        assertEquals( "text/html", response.getContentType() );
        assertTrue( response.getContentAsString().contains( "Some error message" ) );
    }
}
