package com.enonic.wem.portal.script.lib;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.wem.web.servlet.ServletRequestHolder;
import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

import static org.junit.Assert.*;

public class PortalUrlBuilderTest
{
    private HttpServletRequest request;

    private String baseUrl;

    @Before
    public void setup()
    {
        this.request = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( this.request );
        setupRequest( "http", "localhost", 8080, null );
        this.baseUrl = ServletRequestUrlHelper.createUrl( "" );
    }

    @Test
    public void createUrl()
    {
        final PortalUrlBuilder urlBuilder = PortalUrlBuilder.createUrl( baseUrl ).resourcePath( "some/path" );

        assertEquals( "http://localhost:8080/portal/live/some/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithParams()
    {
        final Map<String, Object> params = Maps.newLinkedHashMap();
        params.put( "a", "one" );
        params.put( "b", 2 );
        params.put( "c", null );

        final PortalUrlBuilder urlBuilder = PortalUrlBuilder.createUrl( baseUrl ).resourcePath( "some/path" );
        assertEquals( "http://localhost:8080/portal/live/some/path", urlBuilder.toString() );

        urlBuilder.params( params );
        assertEquals( "http://localhost:8080/portal/live/some/path?a=one&b=2&c=", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithMode()
    {
        final PortalUrlBuilder urlBuilder = PortalUrlBuilder.createUrl( baseUrl ).resourcePath( "some/path" );
        assertEquals( "http://localhost:8080/portal/live/some/path", urlBuilder.toString() );

        urlBuilder.mode( "edit" );
        assertEquals( "http://localhost:8080/portal/edit/some/path", urlBuilder.toString() );
    }

    private void setupRequest( final String scheme, final String host, final int port, final String contextPath )
    {
        Mockito.when( this.request.getScheme() ).thenReturn( scheme );
        Mockito.when( this.request.getServerName() ).thenReturn( host );
        Mockito.when( this.request.getLocalPort() ).thenReturn( port );
        Mockito.when( this.request.getContextPath() ).thenReturn( contextPath );
    }
}
