package com.enonic.wem.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class GeneralUrlBuilderTest
{
    @Test
    public void createContentUrl()
    {
        final GeneralUrlBuilder urlBuilder = new GeneralUrlBuilder().
            baseUri( "/root" ).
            contentPath( "some/path" );

        assertEquals( "/root/portal/live/stage/some/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithParams()
    {
        final GeneralUrlBuilder urlBuilder = new GeneralUrlBuilder().
            contentPath( "some/path" ).
            param( "a", "some thing" ).
            param( "b", 2 ).
            param( "c", null ).
            param( "d", true );

        assertEquals( "/portal/live/stage/some/path?a=some+thing&b=2&c=&d=true", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithMode()
    {
        final GeneralUrlBuilder urlBuilder = new GeneralUrlBuilder().
            mode( "edit" ).
            contentPath( "some/path" );

        assertEquals( "/portal/edit/stage/some/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithWorkspace()
    {
        final GeneralUrlBuilder urlBuilder = new GeneralUrlBuilder().
            workspace( "test" ).
            contentPath( "some/path" );

        assertEquals( "/portal/live/test/some/path", urlBuilder.toString() );
    }

    @Test
    public void createUrlWithOptionPath()
    {
        final GeneralUrlBuilder urlBuilder = new GeneralUrlBuilder().
            contentPath( "some/content/path" ).
            optionPath( "some/option/path" );

        assertEquals( "/portal/live/stage/some/content/path/_/some/option/path", urlBuilder.toString() );
    }
}
