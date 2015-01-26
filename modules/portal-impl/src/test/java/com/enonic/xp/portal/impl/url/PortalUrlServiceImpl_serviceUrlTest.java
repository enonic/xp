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
            context( this.context ).
            service( "myservice" ).
            param( "a", 3 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/portal/stage/context/path/_/service/mymodule/myservice?a=3", url );
    }

    @Test
    public void createUrl_withModule()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            context( this.context ).
            service( "myservice" ).
            module( "othermodule" );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/portal/stage/context/path/_/service/othermodule/myservice", url );
    }
}
