package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.resource.MockResource;
import com.enonic.xp.resource.ResourceKey;
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
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "myapplication" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final AssetUrlParams params = new AssetUrlParams().path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/myproject/draft/_/asset/myapplication:0000000000000001/css/my.css", url );
    }

    @Test
    public void createUrlWithLongContentPath()
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "myapplication" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final StringBuilder longContentPath = new StringBuilder();
        longContentPath.append( "/a".repeat( 10000 ) );
        this.portalRequest.setContentPath( ContentPath.from( longContentPath.toString() ) );
        final AssetUrlParams params = new AssetUrlParams().path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/myproject/draft/_/asset/myapplication:0000000000000001/css/my.css", url );
    }

    @Test
    public void createUrl_withApplication()
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "otherapplication" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 2 ) );

        final AssetUrlParams params = new AssetUrlParams().application( "otherapplication" ).path( "css/my.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/myproject/draft/_/asset/otherapplication:0000000000000002/css/my.css", url );
    }

    @Test
    public void createUrl_withVirtualHost()
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "myapplication" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final AssetUrlParams params = new AssetUrlParams().path( "css/my.css" );

        //Mocks a virtual host and the HTTP request
        final VirtualHost virtualHost = mock( VirtualHost.class );

        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        //Calls the method with a virtual mapping /main -> /
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/" );
        String url = this.service.assetUrl( params );
        assertEquals( "/main/site/myproject/draft/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping /main -> /site/myproject/draft/context
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/site" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/myproject/draft/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping /main -> /site/myproject/draft/context
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping / -> /site/myproject/draft/context
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft/context" );
        url = this.service.assetUrl( params );
        assertEquals( "/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping /main/path -> /site/myproject/draft/context/path
        when( virtualHost.getSource() ).thenReturn( "/main/path" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft/context/path" );
        url = this.service.assetUrl( params );
        assertEquals( "/main/path/_/asset/myapplication:0000000000000001/css/my.css", url );

        //Calls the method with a virtual mapping /site/default/draft/context/path -> /site/default/draft/context/path
        when( virtualHost.getSource() ).thenReturn( "/site/myproject/draft/context/path" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft/context/path" );
        url = this.service.assetUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/asset/myapplication:0000000000000001/css/my.css", url );
    }

    @Test
    public void createUrl_absolute()
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "myapplication" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final AssetUrlParams params = new AssetUrlParams().type( UrlTypeConstants.ABSOLUTE ).path( "css/my.css" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.assetUrl( params );
        assertEquals( "http://localhost/site/myproject/draft/_/asset/myapplication:0000000000000001/css/my.css", url );
    }

    @Test
    public void createUrl_encodeChars()
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "myapplication" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final AssetUrlParams params = new AssetUrlParams().path( "css/my other & strange.css" );

        final String url = this.service.assetUrl( params );
        assertEquals( "/site/myproject/draft/_/asset/myapplication:0000000000000001/css/my%20other%20&%20strange.css", url );
    }

    @Test
    public void createUrlOnVhostMapping()
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "myapplication" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final AssetUrlParams params = new AssetUrlParams().type( UrlTypeConstants.ABSOLUTE ).path( "css/my.css" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft" );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        final String url = this.service.assetUrl( params );
        assertEquals( "http://localhost/_/asset/myapplication:0000000000000001/css/my.css", url );
    }
}
