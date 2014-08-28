package com.enonic.wem.portal.internal.script.lib;


import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class GeneralUrlBuilderTest
    extends BasePortalUrlBuilderTest
{
    @Test
    public void createUrlResource()
    {
        final GeneralUrlBuilder urlBuilder = GeneralUrlBuilder.createUrl( baseUrl ).
            workspace( "test" ).
            resourcePath( "some/path" );

        assertEquals( "/portal/live/test/some/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithParams()
    {
        final Map<String, Object> params = Maps.newLinkedHashMap();
        params.put( "a", "some thing" );
        params.put( "b", 2 );
        params.put( "c", null );

        final GeneralUrlBuilder urlBuilder = GeneralUrlBuilder.createUrl( baseUrl ).
            workspace( "test" ).
            resourcePath( "some/path" );
        assertEquals( "/portal/live/test/some/path", urlBuilder.toString() );

        urlBuilder.params( params ).param( "d", true );
        assertEquals( "/portal/live/test/some/path?a=some+thing&b=2&c=&d=true", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithMode()
    {
        final GeneralUrlBuilder urlBuilder = GeneralUrlBuilder.createUrl( baseUrl ).
            workspace( "test" ).
            contentPath( "some/path" );
        assertEquals( "/portal/live/test/some/path", urlBuilder.toString() );

        urlBuilder.mode( "edit" );
        assertEquals( "/portal/edit/test/some/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithWorkspace()
    {
        final GeneralUrlBuilder urlBuilder = GeneralUrlBuilder.createUrl( baseUrl ).
            workspace( "test" ).
            contentPath( "some/path" );
        assertEquals( "/portal/live/test/some/path", urlBuilder.toString() );

        urlBuilder.workspace( "prod" );
        assertEquals( "/portal/live/prod/some/path", urlBuilder.toString() );
    }


    @Test
    public void createUrlWithService()
    {
        final GeneralUrlBuilder urlBuilder = GeneralUrlBuilder.createUrl( baseUrl ).
            contentPath( "some/content/path" ).
            workspace( "test" ).
            resourceType( "public" ).
            resourcePath( "resource/path" );
        assertEquals( "/portal/live/test/some/content/path/_/public/resource/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlComplex()
    {
        final Map<String, Object> params = Maps.newLinkedHashMap();
        params.put( "two", 2 );
        params.put( "three", 3 );
        final GeneralUrlBuilder urlBuilder = GeneralUrlBuilder.createUrl( baseUrl ).
            mode( "edit" ).
            contentPath( "some/content/path" ).
            workspace( "test" ).
            resourceType( "public" ).
            resourcePath( "resource/path" ).
            param( "one", 1 ).
            params( params );
        assertEquals( "/portal/edit/test/some/content/path/_/public/resource/path?one=1&two=2&three=3", urlBuilder.toString() );
    }
}
