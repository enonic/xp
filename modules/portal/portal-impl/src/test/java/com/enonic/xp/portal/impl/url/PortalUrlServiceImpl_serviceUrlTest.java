package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
        assertEquals( "/site/myproject/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }

    @Test
    public void createUrl_withoutContentPath()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            portalRequest( this.portalRequest ).
            contextPathType( ContextPathType.VHOST.getValue() ).
            service( "myservice" ).
            param( "a", 3 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/site/myproject/draft/_/service/myapplication/myservice?a=3", url );
    }

    @Test
    public void createUrl_withApplication()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            portalRequest( this.portalRequest ).
            service( "myservice" ).
            application( "otherapplication" );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/service/otherapplication/myservice", url );
    }

    @Test
    public void createUrl_absolute()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            service( "myservice" ).
            param( "a", 3 );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "http://localhost/site/myproject/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }

    @Test
    public void createUrl_websocket()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            type( UrlTypeConstants.WEBSOCKET ).
            portalRequest( this.portalRequest ).
            service( "myservice" ).
            param( "a", 3 );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "ws://localhost/site/myproject/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }
}
