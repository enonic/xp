package com.enonic.xp.portal.impl.url;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.util.HashCode;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        assertEquals( "/site/default/draft/_/asset/myapplication:0000000000000001/css/my.css", url );
    }

    @Test
    public void createUrlWithLongContentPath()
    {
        final StringBuilder longContentPath = new StringBuilder();
        longContentPath.append( "/a".repeat( 10000 ) );
        this.portalRequest.setContentPath( ContentPath.from( longContentPath.toString() ) );
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/_/asset/myapplication:0000000000000001/css/my.css", url );
    }

    @Test
    public void createUrl_withContentPath()
    {
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            contextPathType( ContextPathType.RELATIVE.getValue() ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/context/path/_/asset/myapplication:0000000000000001/css/my.css", url );
    }

    @Test
    public void createUrl_withApplication()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "otherapplication" );
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( applicationKey );
        when( this.applicationService.getInstalledApplication( applicationKey ) ).thenReturn( application );
        when( this.resourceService.resourceHash( ResourceKey.assets( applicationKey ) ) ).
            thenReturn( Optional.of( HashCode.fromLong( 2 ) ) );

        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            application( "otherapplication" ).
            path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/_/asset/otherapplication:0000000000000002/css/my.css", url );
    }

    @Test
    public void createUrl_withVirtualHost()
    {
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            path( "css/my.css" );

        //Mocks a virtual host and the HTTP request
        final VirtualHost virtualHost = mock( VirtualHost.class );
        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );
        ServletRequestHolder.setRequest( req );

        //Calls the method with a virtual mapping /main -> /
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/" );
        String url = this.service.assetUrl( params );
        assertEquals( "/main/site/default/draft/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping /main -> /site/default/draft/context
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/site" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/default/draft/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping /main -> /site/default/draft/context
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/site/default/draft" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping / -> /site/default/draft/context
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context" );
        url = this.service.assetUrl( params );
        assertEquals( "/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping /main/path -> /site/default/draft/context/path
        when( virtualHost.getSource() ).thenReturn( "/main/path" );
        when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context/path" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/path/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping /site/default/draft/context/path -> /site/default/draft/context/path
        when( virtualHost.getSource() ).thenReturn( "/site/default/draft/context/path" );
        when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context/path" );
        url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/context/path/_/asset/myapplication:0000000000000001/css/my.css", url );

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

        HttpServletRequest req = mock( HttpServletRequest.class );
        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        ServletRequestHolder.setRequest( req );

        final String url = this.service.assetUrl( params );
        assertEquals( "http://localhost/site/default/draft/_/asset/myapplication:0000000000000001/css/my.css", url );
    }

    @Test
    public void createUrl_encodeChars()
    {
        final AssetUrlParams params = new AssetUrlParams().
            portalRequest( this.portalRequest ).
            path( "css/my other & strange.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/default/draft/_/asset/myapplication:0000000000000001/css/my%20other%20&%20strange.css", url );
    }
}
