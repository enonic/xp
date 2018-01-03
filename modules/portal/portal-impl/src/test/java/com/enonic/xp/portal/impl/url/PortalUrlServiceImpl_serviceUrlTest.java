package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestHolder;

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
    public void createUrl_withoutContentPath()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            portalRequest( this.portalRequest ).
            contextPathType( ContextPathType.VHOST.getValue() ).
            service( "myservice" ).
            param( "a", 3 );

        final String url = this.service.serviceUrl( params );
        assertEquals( "/portal/draft/_/service/myapplication/myservice?a=3", url );
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

    @Test
    public void createUrl_absolute()
    {
        final ServiceUrlParams params = new ServiceUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            service( "myservice" ).
            param( "a", 3 );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        final String url = this.service.serviceUrl( params );
        assertEquals( "http://localhost/portal/draft/context/path/_/service/myapplication/myservice?a=3", url );
    }
}
