package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.servlet.ServletRequestHolder;
import com.enonic.xp.web.vhost.VirtualHost;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PortalUrlServiceImpl_identityUrlTest
    extends AbstractPortalUrlServiceImplTest
{
    @Test
    public void createUrl()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/site/default/draft/_/idprovider/system/login", url );
    }

    @Test
    public void createUrl_withRedirect()
    {
        when( redirectChecksumService.generateChecksum( "https://example.com" ) ).thenReturn( "some-great-checksum" );
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" )
            .redirectionUrl( "https://example.com" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/site/default/draft/_/idprovider/system/login?redirect=https%3A%2F%2Fexample.com&_ticket=some-great-checksum", url );
    }

    @Test
    public void createUrl_withContentPath()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            contextPathType( ContextPathType.RELATIVE.getValue() ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/site/default/draft/context/path/_/idprovider/system/login", url );
    }

    @Test
    public void createUrl_normalizedCharacters()
    {
        this.portalRequest.setContentPath( ContentPath.from( ContentPath.ROOT, "feåtures" ) );

        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        final String url = this.service.identityUrl( params );
        assertEquals( "/site/default/draft/_/idprovider/system/login", url );
    }


    @Test
    public void createUrl_withoutFunction()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            idProviderKey( IdProviderKey.system() );

        final String url = this.service.identityUrl( params );
        assertEquals( "/site/default/draft/_/idprovider/system", url );
    }

    @Test
    public void createUrl_withVirtualHost()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        //Mocks a virtual host and the HTTP request
        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        when( req.getAttribute( VirtualHost.class.getName() ) ).thenReturn( virtualHost );

        //Calls the method with a virtual mapping /main -> /
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/" );
        String url = this.service.identityUrl( params );
        assertEquals( "/main/site/default/draft/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main -> /site/default/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/default/draft/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main -> /site/default/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/default/draft" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping / -> /site/default/draft/context
        Mockito.when( virtualHost.getSource() ).thenReturn( "/" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context" );
        url = this.service.identityUrl( params );
        assertEquals( "/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /main/path -> /site/default/draft/context/path
        Mockito.when( virtualHost.getSource() ).thenReturn( "/main/path" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context/path" );
        url = this.service.identityUrl( params );
        assertEquals( "/main/path/_/idprovider/system/login", url );

        //Calls the method with a virtual mapping /site/default/draft/context/path -> /site/default/draft/context/path
        Mockito.when( virtualHost.getSource() ).thenReturn( "/site/default/draft/context/path" );
        Mockito.when( virtualHost.getTarget() ).thenReturn( "/site/default/draft/context/path" );
        url = this.service.identityUrl( params );
        assertEquals( "/site/default/draft/context/path/_/idprovider/system/login", url );

        //Post treatment
        ServletRequestHolder.setRequest( null );
    }

    @Test
    public void createUrl_absolute()
    {
        final IdentityUrlParams params = new IdentityUrlParams().
            portalRequest( this.portalRequest ).
            type( UrlTypeConstants.ABSOLUTE ).
            idProviderKey( IdProviderKey.system() ).
            idProviderFunction( "login" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final String url = this.service.identityUrl( params );
        assertEquals( "http://localhost/site/default/draft/_/idprovider/system/login", url );
    }

    @Test
    public void createUrlForSlashApiWithVhostContextConfig()
    {
        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );

        final IdentityUrlParams params = new IdentityUrlParams().portalRequest( this.portalRequest )
            .type( UrlTypeConstants.ABSOLUTE )
            .idProviderKey( IdProviderKey.system() )
            .idProviderFunction( "login" );

        Context context = ContextBuilder.create().build();
        context.getLocalScope().setAttribute( "idProviderService.baseUrl", "http://media.enonic.com" );

        String url = context.callWith( () -> this.service.identityUrl( params ) );
        assertEquals( "http://media.enonic.com/system/login", url );
    }

    @Test
    public void createUrlForSlashApi()
    {
        this.portalRequest.setBaseUri( "" );
        this.portalRequest.setRawPath( "/api/com.enonic.app.appname" );

        when( req.getServerName() ).thenReturn( "localhost" );
        when( req.getScheme() ).thenReturn( "http" );
        when( req.getServerPort() ).thenReturn( 80 );

        final IdentityUrlParams params = new IdentityUrlParams().portalRequest( this.portalRequest )
            .type( UrlTypeConstants.ABSOLUTE )
            .idProviderKey( IdProviderKey.system() )
            .idProviderFunction( "login" );

        Context context = ContextBuilder.create().build();
        String url = context.callWith( () -> this.service.identityUrl( params ) );
        assertEquals( "http://localhost/api/idprovider/system/login", url );
    }
}
