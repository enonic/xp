package com.enonic.wem.portal.script.lib;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;
import com.enonic.wem.core.web.servlet.ServletRequestUrlHelper;

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
        this.baseUrl = ServletRequestUrlHelper.createUri( "" );
    }

    @Test
    public void createUrlResource()
    {
        final PortalUrlBuilder urlBuilder = PortalUrlBuilder.createUrl( baseUrl ).resourcePath( "some/path" );

        assertEquals( "/portal/live/some/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithParams()
    {
        final Map<String, Object> params = Maps.newLinkedHashMap();
        params.put( "a", "some thing" );
        params.put( "b", 2 );
        params.put( "c", null );

        final PortalUrlBuilder urlBuilder = PortalUrlBuilder.createUrl( baseUrl ).resourcePath( "some/path" );
        assertEquals( "/portal/live/some/path", urlBuilder.toString() );

        urlBuilder.params( params ).param( "d", true );
        assertEquals( "/portal/live/some/path?a=some+thing&b=2&c=&d=true", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithMode()
    {
        final PortalUrlBuilder urlBuilder = PortalUrlBuilder.createUrl( baseUrl ).contentPath( "some/path" );
        assertEquals( "/portal/live/some/path", urlBuilder.toString() );

        urlBuilder.mode( "edit" );
        assertEquals( "/portal/edit/some/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithService()
    {
        final PortalUrlBuilder urlBuilder =
            PortalUrlBuilder.createUrl( baseUrl ).contentPath( "some/content/path" ).resourceType( "public" ).resourcePath(
                "resource/path" );
        assertEquals( "/portal/live/some/content/path/_/public/resource/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlComplex()
    {
        final Map<String, Object> params = Maps.newLinkedHashMap();
        params.put( "two", 2 );
        params.put( "three", 3 );
        final PortalUrlBuilder urlBuilder = PortalUrlBuilder.createUrl( baseUrl ).
            mode( "edit" ).
            contentPath( "some/content/path" ).
            resourceType( "public" ).
            resourcePath( "resource/path" ).
            param( "one", 1 ).
            params( params );
        assertEquals( "/portal/edit/some/content/path/_/public/resource/path?one=1&two=2&three=3",
                      urlBuilder.toString() );
    }

    private void setupRequest( final String scheme, final String host, final int port, final String contextPath )
    {
        Mockito.when( this.request.getScheme() ).thenReturn( scheme );
        Mockito.when( this.request.getServerName() ).thenReturn( host );
        Mockito.when( this.request.getLocalPort() ).thenReturn( port );
        Mockito.when( this.request.getContextPath() ).thenReturn( contextPath );
    }
}
