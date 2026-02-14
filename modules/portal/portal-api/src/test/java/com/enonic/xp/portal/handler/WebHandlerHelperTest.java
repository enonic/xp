package com.enonic.xp.portal.handler;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.enonic.xp.web.WebException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class WebHandlerHelperTest
{
    @Test
    void testFindApiPath_withEndpointPath()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/site/draft/_/com.enonic.app:myapi/some/path" );
        assertEquals( "/some/path", WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_withEndpointPath_empty()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/site/draft/_/com.enonic.app:myapi" );
        assertEquals( "", WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_withoutEndpointPath()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/api/com.enonic.app:myapi/some/path" );
        assertEquals( "/some/path", WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_withoutEndpointPath_empty()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/api/com.enonic.app:myapi" );
        assertEquals( "", WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_invalidEndpointPath()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/site/draft/_/com.enonic.app:myapiextra" );
        assertThrows( WebException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_invalidBasePath()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/api/com.enonic.app:myapiextra" );
        assertThrows( WebException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }
}
