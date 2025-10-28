package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.resource.MockResource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalUrlServiceImpl_identityUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    void createUrl()
    {
        final IdentityUrlParams params = new IdentityUrlParams().idProviderKey( IdProviderKey.system() ).idProviderFunction( "login" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/_/idprovider/system/login", url );
    }

    @Test
    void createUrl_withRedirect()
    {
        when( redirectChecksumService.generateChecksum( "https://example.com" ) ).thenReturn( "some-great-checksum" );
        final IdentityUrlParams params = new IdentityUrlParams().idProviderKey( IdProviderKey.system() )
            .idProviderFunction( "login" )
            .redirectionUrl( "https://example.com" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/_/idprovider/system/login?redirect=https%3A%2F%2Fexample.com&_ticket=some-great-checksum", url );
    }

    @Test
    void createUrl_withContentPath()
    {
        final IdentityUrlParams params = new IdentityUrlParams().contextPathType( ContextPathType.RELATIVE.getValue() )
            .idProviderKey( IdProviderKey.system() )
            .idProviderFunction( "login" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/_/idprovider/system/login", url );
    }

    @Test
    void createUrl_normalizedCharacters()
    {
        this.portalRequest.setContentPath( ContentPath.from( ContentPath.ROOT, "feåtures" ) );

        final IdentityUrlParams params = new IdentityUrlParams().idProviderKey( IdProviderKey.system() ).idProviderFunction( "login" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/_/idprovider/system/login", url );
    }


    @Test
    void createUrl_withoutFunction()
    {
        final IdentityUrlParams params = new IdentityUrlParams().idProviderKey( IdProviderKey.system() );

        final String url = this.service.identityUrl( params );
        assertEquals( "/_/idprovider/system", url );
    }

    @Test
    void createUrl_withVirtualHost()
    {
        final IdentityUrlParams params = new IdentityUrlParams().idProviderKey( IdProviderKey.system() ).idProviderFunction( "login" );

        //Mocks a virtual host and the HTTP request
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        //Calls the method with a virtual mapping /main -> /
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/" );
        String url = this.service.identityUrl( params );
        assertEquals( "/main/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main -> /site/default/draft/context
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/site" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main -> /site/default/draft/context
        when( virtualHost.getSource() ).thenReturn( "/main" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping / -> /site/default/draft/context
        when( virtualHost.getSource() ).thenReturn( "/" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft/context" );
        url = this.service.identityUrl( params );
        assertEquals( "/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main/path -> /site/default/draft/context/path
        when( virtualHost.getSource() ).thenReturn( "/main/path" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft/context/path" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/path/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /site/default/draft/context/path -> /site/default/draft/context/path
        when( virtualHost.getSource() ).thenReturn( "/site/myproject/draft/context/path" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/draft/context/path" );
        url = this.service.identityUrl( params );
        assertEquals( "/site/myproject/draft/context/path/_/idprovider/system/login", url );
    }

    @Test
    void createUrl_absolute()
    {
        final IdentityUrlParams params =
            new IdentityUrlParams().type( UrlTypeConstants.ABSOLUTE ).idProviderKey( IdProviderKey.system() ).idProviderFunction( "login" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.identityUrl( params );
        assertEquals( "http://localhost/_/idprovider/system/login", url );
    }

    @Test
    void createUrlOnVhostMapping()
    {
        final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "myapplication" ), "META-INF/MANIFEST.MF" );
        when( this.resourceService.getResource( resourceKey ) ).thenReturn( MockResource.empty( resourceKey, 1 ) );

        final IdentityUrlParams params =
            new IdentityUrlParams().type( UrlTypeConstants.ABSOLUTE ).idProviderKey( IdProviderKey.system() ).idProviderFunction( "login" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getSource() ).thenReturn( "/source" );
        when( virtualHost.getTarget() ).thenReturn( "/site/myproject/master/mysite" );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        String url = this.service.identityUrl( params );
        assertEquals( "http://localhost/source/_/idprovider/system/login", url );
    }
}
