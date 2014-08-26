package com.enonic.wem.portal.internal.url;

import org.junit.Test;

import com.enonic.wem.portal.PortalUrlBuilder;
import com.enonic.wem.portal.url.ServiceUrlBuilder;

import static org.junit.Assert.*;

public class ServiceUrlBuilderTest
    extends BasePortalUrlBuilderTest
{

    @Test
    public void createServiceUrlResource()
    {
        final ServiceUrlBuilder urlBuilder = PortalUrlBuilder.createServiceUrl( baseUrl ).
            mode( "live" ).
            contentPath( "path/to/content" ).
            module( "demo-1.0.0" ).
            serviceName( "mytest" ).
            param( "a", "b" );

        assertEquals( "/portal/live/path/to/content/_/service/demo-1.0.0/mytest?a=b", urlBuilder.toString() );
    }

}