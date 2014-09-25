package com.enonic.wem.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class PublicUrlBuilderTest
{
    @Test
    public void createPublicUrl()
    {
        final PublicUrlBuilder urlBuilder = new PublicUrlBuilder().
            baseUri( "/root" ).
            contentPath( "some/path" ).
            module( "mymodule" ).
            resourcePath( "css/my.css" );

        assertEquals( "/root/portal/live/stage/some/path/_/public/mymodule/css/my.css", urlBuilder.toString() );
    }
}

