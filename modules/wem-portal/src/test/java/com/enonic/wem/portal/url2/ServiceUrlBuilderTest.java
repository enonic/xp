package com.enonic.wem.portal.url2;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceUrlBuilderTest
{
    @Test
    public void createServiceUrl()
    {
        final ServiceUrlBuilder urlBuilder = new ServiceUrlBuilder().
            baseUri( "/root" ).
            contentPath( "some/path" ).
            module( "mymodule-1.0.0" ).
            serviceName( "myservice" ).
            param( "a", 3 );

        assertEquals( "/root/portal/live/stage/some/path/_/service/mymodule-1.0.0/myservice?a=3", urlBuilder.toString() );
    }
}
