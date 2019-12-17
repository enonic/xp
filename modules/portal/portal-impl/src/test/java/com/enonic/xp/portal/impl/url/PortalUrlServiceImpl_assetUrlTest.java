package com.enonic.xp.portal.impl.url;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PortalUrlServiceImpl_assetUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/_/asset/myapplication:31556889864403199/css/my.css", url );
    }

    @Test
    public void createUrlWithLongContentPath()
    {
        final StringBuilder longContentPath = new StringBuilder();
        for ( int i = 0; i < 10000; i++ )
        {
            longContentPath.append( "/a" );
        }
        this.portalRequest.setContentPath( ContentPath.from( longContentPath.toString() ) );
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/_/asset/myapplication:31556889864403199/css/my.css", url );
    }
    
    @Test
    public void createUrl_withContentPath()
    {
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            contextPathType( ContextPathType.RELATIVE.getValue() ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/context/path/_/asset/myapplication:31556889864403199/css/my.css", url );
    }

    @Test
    public void createUrl_withApplication()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "otherapplication" );
        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( applicationKey );
        Mockito.when( application.getModifiedTime() ).thenReturn( Instant.MAX );
        Mockito.when( this.applicationService.getInstalledApplication( applicationKey ) ).thenReturn( application );

        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            application( "otherapplication" ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/_/asset/otherapplication:31556889864403199/css/my.css", url );
    }

    @Test
    public void createUrl_withVirtualHost()
    {
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            path( "css/my.css" );

        //Mocks a virtual host and the HTTP request
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        VirtualHostHelper.setVirtualHost( req, virtualHost );

        //Calls the method with a virtual mapping /main -> /
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/" );
        String url = this.service.assetUrl( params );
        assertEquals( "/main/site/default/draft/_/asset/myapplication:31556889864403199/css/my.css", url );

        //Calls the method with a virtual mapping /main -> /site/default/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/default/draft/_/asset/myapplication:31556889864403199/css/my.css", url );

        //Calls the method with a virtual mapping /main -> /site/default/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/default/draft" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/_/asset/myapplication:31556889864403199/css/my.css", url );

        //Calls the method with a virtual mapping / -> /site/default/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context" );
        url = this.service.assetUrl( params );
        assertEquals( "/_/asset/myapplication:31556889864403199/css/my.css", url );

        //Calls the method with a virtual mapping /main/path -> /site/default/draft/context/path
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main/path" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context/path" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/path/_/asset/myapplication:31556889864403199/css/my.css", url );

        //Calls the method with a virtual mapping /site/default/draft/context/path -> /site/default/draft/context/path
        Mockito.when( virtualHost.getSource() ).thenReturn( "/site/default/draft/context/path" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context/path" );
        url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/context/path/_/asset/myapplication:31556889864403199/css/my.css", url );

        //Post treatment
        ServletRequestHolder.setRequest( null );
    }

    @Test
    public void createUrl_absolute()
    {
        final AssetUrlParams params = new AssetUrlParams().
            type( UrlTypeConstants.ABSOLUTE ).
            portalRequest( this.portalRequest ).
            path( "css/my.css" );

        MockHttpServletRequest req = new MockHttpServletRequest();
        ServletRequestHolder.setRequest( req );

        final String url = this.service.assetUrl( params );
        assertEquals( "http://localhost/site/default/draft/_/asset/myapplication:31556889864403199/css/my.css", url );
    }

    @Test
    public void createUrl_encodeChars()
    {
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            path( "css/my other & strange.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/_/asset/myapplication:31556889864403199/css/my%20other%20&%20strange.css", url );
    }
}
