package com.enonic.xp.portal.impl.url;

import org.junit.Test;

import com.enonic.xp.portal.url.ServiceUrlParams;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_serviceUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            portalRequest( this.portalRequest ).
            service( "myservice" ).
            param( "a", 3 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/portal/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }

    @Test
    public void createUrl_withApplication()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            portalRequest( this.portalRequest ).
            service( "myservice" ).
            application( "otherapplication" );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/portal/draft/context/path/_/service/otherapplication/myservice", url );
    }
}
