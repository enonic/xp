package com.enonic.xp.portal.impl.mapper;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.MapSerializableAssert;
import com.enonic.xp.portal.impl.idprovider.AccessTokenFilter;
import com.enonic.xp.portal.impl.idprovider.DeviceLoginHandler;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.token.AccessTokenClaims;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class PortalRequestMapperTest
{
    private PortalRequest portalRequest;

    private final MapSerializableAssert assertHelper = new MapSerializableAssert( PortalRequestMapperTest.class );

    @BeforeEach
    void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setScheme( "http" );
        this.portalRequest.setHost( "localhost" );
        this.portalRequest.setPort( 80 );
        this.portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        this.portalRequest.setBranch( ContentConstants.BRANCH_DRAFT );
        this.portalRequest.setRemoteAddress( "10.0.0.1" );
        this.portalRequest.setPath( "/site/live/master/a/b" );
        this.portalRequest.setRawPath( "/site/live/master/a/b" );
        this.portalRequest.setContextPath( "/site/live/master/a" );
        this.portalRequest.setUrl( "http://localhost/site/live/master/a/b?param1=value1" );
        this.portalRequest.setValidTicket( Boolean.TRUE );
        this.portalRequest.getParams().put( "param1", "value1" );
        this.portalRequest.getParams().put( "param2", "value2" );
        this.portalRequest.getParams().put( "param3", "value3-A" );
        this.portalRequest.getParams().put( "param3", "value3-B" );

        this.portalRequest.getHeaders().put( "header1", "value1" );
        this.portalRequest.getHeaders().put( "header2", "value2" );
        this.portalRequest.getHeaders().put( "header3", "value3" );

        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setContent( ContentFixtures.newContent() );
        this.portalRequest.setSite( ContentFixtures.newSite() );
        this.portalRequest.setPageDescriptor( ContentFixtures.newPageDescriptor() );
    }

    @Test
    void testSimple()
    {
        assertHelper.assertJson( "request-simple.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void testIdProviderSettings()
    {
        final VirtualHostIdProvider settings = VirtualHostIdProvider.create()
            .flows( Set.of( "basic", "autologin" ) )
            .scopes( Map.of( "profile", VirtualHostIdProvider.Scope.of( PrincipalKeys.empty() ), "content:read",
                             new VirtualHostIdProvider.Scope(
                                 PrincipalKeys.from( PrincipalKey.ofRole( "cms.project.site.viewer" ), RoleKeys.CONTENT_MANAGER_APP ), true ) ) )
            .build();
        setIdProviderSettings( IdProvider.create().key( IdProviderKey.system() ).build(), settings, Set.of( "api" ) );

        // every list is serialized sorted
        assertHelper.assertJson( "request-idprovider-settings.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void testIdProviderSettings_noRestriction()
    {
        setIdProviderSettings( IdProvider.create().key( IdProviderKey.system() ).build(), VirtualHostIdProvider.create().build(), Set.of() );

        // mapped with nothing set: empty flows mean no restriction, and the id provider app applies its own defaults
        assertHelper.assertJson( "request-idprovider-settings-empty.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void testIdProviderSettings_notMapped()
    {
        setIdProviderSettings( IdProvider.create().key( IdProviderKey.from( "other" ) ).build(), VirtualHostIdProvider.create().build(),
                               Set.of() );
        assertHelper.assertJson( "request-simple.json", new PortalRequestMapper( this.portalRequest ) );

        setIdProviderSettings( IdProvider.create().build(), VirtualHostIdProvider.create().build(), Set.of() );
        assertHelper.assertJson( "request-simple.json", new PortalRequestMapper( this.portalRequest ) );

        this.portalRequest.setIdProvider( IdProvider.create().key( IdProviderKey.system() ).build() );
        this.portalRequest.setVirtualHost( null );
        assertHelper.assertJson( "request-simple.json", new PortalRequestMapper( this.portalRequest ) );
    }

    private void setIdProviderSettings( final IdProvider idProvider, final VirtualHostIdProvider settings, final Set<String> audience )
    {
        this.portalRequest.setIdProvider( idProvider );

        final VirtualHost virtualHost = Mockito.mock( VirtualHost.class );
        Mockito.when( virtualHost.getIdProviders() ).thenReturn( Map.of( IdProviderKey.system(), settings ) );
        Mockito.when( virtualHost.getAudience() ).thenReturn( audience );
        this.portalRequest.setVirtualHost( virtualHost );
    }

    @Test
    void testAccessToken()
    {
        final AccessTokenClaims claims = AccessTokenClaims.create()
            .issuer( "xp" )
            .subject( PrincipalKey.ofUser( IdProviderKey.system(), "alice" ) )
            .audience( Set.of( "api" ) )
            .scope( Set.of( "content:write", "content:read" ) )
            .clientId( "my-cli" )
            .grantId( "grant-1" )
            .issuedAt( Instant.parse( "2026-10-14T12:00:00Z" ) )
            .expiresAt( Instant.parse( "2026-10-14T13:00:00Z" ) )
            .tokenId( "jti" )
            .build();
        final HttpServletRequest rawRequest = Mockito.mock( HttpServletRequest.class );
        Mockito.when( rawRequest.getAttribute( AccessTokenFilter.CLAIMS_ATTRIBUTE ) ).thenReturn( claims );
        this.portalRequest.setRawRequest( rawRequest );

        assertHelper.assertJson( "request-access-token.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void testDeviceLogin()
    {
        final Map<String, Object> context = new java.util.LinkedHashMap<>();
        context.put( "idProvider", "system" );
        context.put( "action", "confirm" );
        context.put( "userCode", "BCDF-GHJK" );
        final Map<String, Object> scope = new java.util.LinkedHashMap<>();
        scope.put( "id", "content:read" );
        scope.put( "roles", java.util.List.of( "role:cms.cm.app" ) );
        scope.put( "required", true );
        context.put( "scope", java.util.List.of( scope ) );
        final HttpServletRequest rawRequest = Mockito.mock( HttpServletRequest.class );
        Mockito.when( rawRequest.getAttribute( DeviceLoginHandler.CONTEXT_ATTRIBUTE ) ).thenReturn( context );
        this.portalRequest.setRawRequest( rawRequest );

        assertHelper.assertJson( "request-device-login.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void testCookies()
    {
        this.portalRequest.getCookies().put( "a", "1" );
        this.portalRequest.getCookies().put( "b", "2" );

        assertHelper.assertJson( "request-cookies.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void testBody()
    {
        this.portalRequest.setMethod( HttpMethod.POST );
        this.portalRequest.setContentType( "text/plain" );
        this.portalRequest.setBody( "Hello World" );

        assertHelper.assertJson( "request-body.json", new PortalRequestMapper( this.portalRequest ) );
    }

    @Test
    void contentPath()
    {
        this.portalRequest.setContentPath( ContentPath.from( "/a/b" ) );
        final ScriptValue value = MapSerializableAssert.serializeJs( new PortalRequestMapper( this.portalRequest ) );
        assertEquals( "/a/b", value.getMember( "contentPath" ).getValue( String.class ) );
    }

    @Test
    void getHeader()
    {
        final ScriptValue value = MapSerializableAssert.serializeJs( new PortalRequestMapper( this.portalRequest ) );
        assertEquals( "value1", value.getMember( "getHeader" ).call( "Header1" ).getValue( String.class ) );
        assertEquals( "value1", value.getMember( "getHeader" ).call( "header1" ).getValue( String.class ) );
        assertEquals( "value2", value.getMember( "getHeader" ).call( "hEader2" ).getValue( String.class ) );
        assertNull( value.getMember( "getHeader" ).call( "header4" ) );
    }
}
