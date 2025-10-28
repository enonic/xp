package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PortalUrlServiceImpl_serviceUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    void createUrl()
    {
        final ServiceUrlParams params = new ServiceUrlParams().service( "myservice" ).param( "a", 3 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }

    @Test
    void createUrl_withoutContentPath()
    {
        final ServiceUrlParams params = new ServiceUrlParams().service( "myservice" ).param( "a", 3 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }

    @Test
    void createUrl_withApplication()
    {
        final ServiceUrlParams params = new ServiceUrlParams().service( "myservice" ).application( "otherapplication" );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/service/otherapplication/myservice", url );
    }

    @Test
    void createUrl_absolute()
    {
        final ServiceUrlParams params = new ServiceUrlParams().type( UrlTypeConstants.ABSOLUTE ).service( "myservice" ).param( "a", 3 );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "http://localhost/site/myproject/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }

    @Test
    void createUrl_websocket()
    {
        final ServiceUrlParams params = new ServiceUrlParams().type( UrlTypeConstants.WEBSOCKET ).service( "myservice" ).param( "a", 3 );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "ws://localhost/site/myproject/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }
}
