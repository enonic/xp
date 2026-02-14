package com.enonic.xp.portal.handler;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.dispatch.DispatchConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        assertThrows( IllegalArgumentException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_invalidBasePath()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/api/com.enonic.app:myapiextra" );
        assertThrows( IllegalArgumentException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_invalidBasePath2()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/api/myapi" );
        assertThrows( IllegalArgumentException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_apiConnector()
    {
        final WebRequest req = createApiConnectorRequest( "/com.enonic.app:myapi/some/path" );
        assertEquals( "/some/path", WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_apiConnector_empty()
    {
        final WebRequest req = createApiConnectorRequest( "/com.enonic.app:myapi" );
        assertEquals( "", WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_apiConnector_invalid()
    {
        final WebRequest req = createApiConnectorRequest( "/com.enonic.app:myapiextra" );
        assertThrows( IllegalArgumentException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_withoutEndpointPath_wrongPrefix()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/other/com.enonic.app:myapi" );
        assertThrows( IllegalArgumentException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_withEndpointPath_wrongPrefix()
    {
        final WebRequest req = new WebRequest();
        req.setRawPath( "/site/draft/_/other:api" );
        assertThrows( IllegalArgumentException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    @Test
    void testFindApiPath_apiConnector_wrongPrefix()
    {
        final WebRequest req = createApiConnectorRequest( "com.enonic.app:myapi" );
        assertThrows( IllegalArgumentException.class, () -> WebHandlerHelper.findApiPath( req, "com.enonic.app:myapi" ) );
    }

    private static WebRequest createApiConnectorRequest( final String rawPath )
    {
        final HttpServletRequest rawRequest = mock( HttpServletRequest.class );
        when( rawRequest.getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE ) ).thenReturn( DispatchConstants.API_CONNECTOR );

        final WebRequest req = new WebRequest();
        req.setRawRequest( rawRequest );
        req.setRawPath( rawPath );
        return req;
    }
}
