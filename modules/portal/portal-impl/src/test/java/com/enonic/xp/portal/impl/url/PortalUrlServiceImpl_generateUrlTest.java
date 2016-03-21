package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.portal.url.GenerateUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_generateUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final GenerateUrlParams params = new GenerateUrlParams().
            portalRequest( this.portalRequest ).
            url( "/admin" ).
            param( "a", 3 );

        final String url = this.service.generateUrl( params );
        assertEquals( "/admin?a=3", url );
    }

    @Test
    public void createUrl_absolute()
    {
        final GenerateUrlParams params = new GenerateUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            url( "/admin" ).
            param( "a", 3 );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        final String url = this.service.generateUrl( params );
        assertEquals( "http://localhost/admin?a=3", url );
    }

    @Test
    public void createUrl_withVirtualHost()
    {
        final GenerateUrlParams params = new GenerateUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            url( "/admin" ).
            param( "a", 3 );

        //Mocks a virtual host and the HTTP request
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        VirtualHostHelper.setVirtualHost( req, virtualHost );

        //Calls the method with a virtual mapping /main -> /
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/" );
        String url = this.service.generateUrl( params );
        assertEquals( "http://localhost/main/admin?a=3", url );

        //Calls the method with a virtual mapping /main -> /portal/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/studio" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/admin" );
        url = this.service.generateUrl( params );
        assertEquals( "http://localhost/studio?a=3", url );

        //Calls the method with a virtual mapping /main -> /portal/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/admin" );
        url = this.service.generateUrl( params );
        assertEquals( "http://localhost/?a=3", url );

        //Post treatment
        ServletRequestHolder.setRequest( null );
    }
}
