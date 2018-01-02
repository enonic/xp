package com.enonic.xp.portal.impl.url;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.portal.url.ApplicationUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class PortalUrlServiceImpl_applicationUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final ApplicationUrlParams params = new ApplicationUrlParams().
            portalRequest( this.portalRequest ).
            path( "/subpath" ).
            param( "a", 3 );

        final String url = this.service.applicationUrl( params );
        assertEquals( "/app/myapplication/subpath?a=3", url );
    }

    @Test
    public void createUrl_withApplication()
    {
        final ApplicationUrlParams params = new ApplicationUrlParams().
            portalRequest( this.portalRequest ).
            application( "otherapplication" ).
            path( "/subpath" ).
            param( "a", 3 );

        final String url = this.service.applicationUrl( params );
        assertEquals( "/app/otherapplication/subpath?a=3", url );
    }

    @Test
    public void createUrl_absolute()
    {
        final ApplicationUrlParams params = new ApplicationUrlParams().
            portalRequest( this.portalRequest ).
            type( UrlTypeConstants.ABSOLUTE );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        final String url = this.service.applicationUrl( params );
        assertEquals( "http://localhost/app/myapplication", url );
    }
}
