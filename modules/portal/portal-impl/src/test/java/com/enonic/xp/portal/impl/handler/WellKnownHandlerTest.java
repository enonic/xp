package com.enonic.xp.portal.impl.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.impl.idprovider.OAuthMetadataService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WellKnownHandlerTest
{
    private final SecurityService securityService = mock( SecurityService.class );

    private final WebHandlerChain chain = mock( WebHandlerChain.class );

    private final VirtualHostService virtualHostService = mock( VirtualHostService.class );

    private OAuthMetadataService metadata;

    private WellKnownHandler handler;

    @BeforeEach
    void setUp()
    {
        when( securityService.getScopes( any() ) ).thenReturn( Scopes.empty() );
        metadata = new OAuthMetadataService( securityService, virtualHostService );
        handler = new WellKnownHandler( metadata );
    }

    private static WebRequest request( final String path, final String connector, final VirtualHost mapping )
    {
        return request( path, path, connector, mapping );
    }

    /**
     * @param path          the path decoded, as the request carries it
     * @param requestedPath the path as the client sent it
     */
    private static WebRequest request( final String path, final String requestedPath, final String connector, final VirtualHost mapping )
    {
        final WebRequest request = new WebRequest();
        request.setMethod( HttpMethod.GET );
        request.setScheme( "https" );
        request.setHost( "h" );
        request.setPort( 443 );
        request.setRawPath( path );
        request.setVirtualHost( mapping );
        final HttpServletRequest raw = mock( HttpServletRequest.class );
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put( DispatchConstants.CONNECTOR_ATTRIBUTE, connector );
        doAnswer( i -> attributes.put( i.getArgument( 0 ), i.getArgument( 1 ) ) ).when( raw ).setAttribute( any(), any() );
        when( raw.getAttribute( any() ) ).thenAnswer( i -> attributes.get( i.<String>getArgument( 0 ) ) );
        VirtualHostHelper.setRequestedPath( raw, requestedPath );
        request.setRawRequest( raw );
        return request;
    }

    private static VirtualHost mapping( final String source, final IdProviderKey idProvider )
    {
        final VirtualHost mapping = mock( VirtualHost.class );
        when( mapping.getConnector() ).thenReturn( DispatchConstants.WEB_CONNECTOR );
        when( mapping.getSource() ).thenReturn( source );
        when( mapping.getDefaultIdProviderKey() ).thenReturn( idProvider );
        when( mapping.getIdProviders() ).thenReturn( idProvider == null ? Map.of() : Map.of( idProvider, VirtualHostIdProvider.create().allFlows().build() ) );
        return mapping;
    }

    @Test
    void serves_the_two_documents_on_the_web_connector_only()
        throws Exception
    {
        final VirtualHost root = mapping( "/", IdProviderKey.system() );
        final WebRequest resource = request( "/.well-known/oauth-protected-resource/api/mcp", DispatchConstants.WEB_CONNECTOR, root );
        assertThat( handler.canHandle( resource ) ).isTrue();
        final WebResponse answered = handler.handle( resource, WebResponse.create().build(), chain );
        assertThat( answered.getStatus() ).isEqualTo( HttpStatus.OK );
        assertThat( answered.getHeaders() ).containsEntry( "Cache-Control", "public, max-age=300" );
        @SuppressWarnings("unchecked") final Map<String, Object> body = (Map<String, Object>) answered.getBody();
        assertThat( body ).containsEntry( "resource", "https://h/api/mcp" )
            .containsEntry( "authorization_servers", List.of( "https://h/_/idprovider/system" ) );
        @SuppressWarnings("unchecked") final Map<String, Object> slashed = (Map<String, Object>) handler.handle(
            request( "/.well-known/oauth-protected-resource/api/mcp/", DispatchConstants.WEB_CONNECTOR, root ), WebResponse.create().build(),
            chain ).getBody();
        assertThat( slashed ).as( "the resource is named as it was requested" ).containsEntry( "resource", "https://h/api/mcp/" );

        final WebRequest server = request( "/.well-known/oauth-authorization-server/_/idprovider/system", DispatchConstants.WEB_CONNECTOR, root );
        @SuppressWarnings("unchecked") final Map<String, Object> serverBody =
            (Map<String, Object>) handler.handle( server, WebResponse.create().build(), chain ).getBody();
        assertThat( serverBody ).containsEntry( "issuer", "https://h/_/idprovider/system" )
            .containsEntry( "token_endpoint", "https://h/_/idprovider/system/token" );

        assertThat( handler.canHandle( request( "/.well-known/oauth-protected-resource", DispatchConstants.MANAGEMENT_CONNECTOR, root ) ) ).isFalse();
        assertThat( handler.canHandle( request( "/.well-known/openid-configuration", DispatchConstants.WEB_CONNECTOR, root ) ) ).isFalse();
        assertThat( handler.canHandle( request( "/api/mcp", DispatchConstants.WEB_CONNECTOR, root ) ) ).isFalse();
    }

    @Test
    void the_resource_is_named_as_the_client_sent_it()
        throws Exception
    {
        final WebRequest accented = request( "/.well-known/oauth-protected-resource/api/caf\u00e9", "/.well-known/oauth-protected-resource/api/caf%C3%A9",
                                             DispatchConstants.WEB_CONNECTOR, mapping( "/", IdProviderKey.system() ) );
        @SuppressWarnings("unchecked") final Map<String, Object> body =
            (Map<String, Object>) handler.handle( accented, WebResponse.create().build(), chain ).getBody();
        assertThat( body ).containsEntry( "resource", "https://h/api/caf%C3%A9" );
    }

    @Test
    void nothing_to_describe_is_not_found()
    {
        final WebRequest noIdProvider = request( "/.well-known/oauth-protected-resource", DispatchConstants.WEB_CONNECTOR, mapping( "/", null ) );
        assertThatThrownBy( () -> handler.handle( noIdProvider, WebResponse.create().build(), chain ) ).isInstanceOf( WebException.class )
            .extracting( e -> ( (WebException) e ).getStatus() )
            .isEqualTo( HttpStatus.NOT_FOUND );

        final WebRequest notAServer = request( "/.well-known/oauth-authorization-server/api/mcp", DispatchConstants.WEB_CONNECTOR,
                                               mapping( "/", IdProviderKey.system() ) );
        assertThatThrownBy( () -> handler.handle( notAServer, WebResponse.create().build(), chain ) ).isInstanceOf( WebException.class );

        final WebRequest noMapping = request( "/.well-known/oauth-protected-resource", DispatchConstants.WEB_CONNECTOR, null );
        assertThatThrownBy( () -> handler.handle( noMapping, WebResponse.create().build(), chain ) ).isInstanceOf( WebException.class );
    }

    private static VirtualHost webMapping( final String host, final int order, final IdProviderKey idProvider, final Set<String> flows )
    {
        final VirtualHost mapping = mock( VirtualHost.class );
        when( mapping.getConnector() ).thenReturn( DispatchConstants.WEB_CONNECTOR );
        when( mapping.getHost() ).thenReturn( host );
        when( mapping.getSource() ).thenReturn( "/" );
        when( mapping.getOrder() ).thenReturn( order );
        when( mapping.getIdProviders() ).thenReturn( Map.of( idProvider, VirtualHostIdProvider.create().flows( flows ).build() ) );
        return mapping;
    }

    private static VirtualHost managementMapping( final IdProviderKey idProvider )
    {
        final VirtualHost mapping = mock( VirtualHost.class );
        when( mapping.getConnector() ).thenReturn( DispatchConstants.MANAGEMENT_CONNECTOR );
        when( mapping.getSource() ).thenReturn( "/" );
        when( mapping.getDefaultIdProviderKey() ).thenReturn( idProvider );
        when( mapping.getIdProviders() ).thenReturn( Map.of( idProvider, VirtualHostIdProvider.create().flows( Set.of( "autologin" ) ).build() ) );
        return mapping;
    }

    @Test
    void a_management_mapping_names_the_web_mapping_that_issues_its_tokens()
    {
        final IdProviderKey cloud = IdProviderKey.from( "enonic-cloud" );
        final List<VirtualHost> mappings =
            List.of( webMapping( "~.*\\.example\\.com", 1, cloud, Set.of( "device" ) ), webMapping( "a.example.com b.example.com", 2, cloud, Set.of( "device" ) ),
                     webMapping( "site.example.com", 3, cloud, Set.of( "login" ) ), webMapping( "later.example.com", 20, cloud, Set.of( "code" ) ),
                     webMapping( "admin.example.com", 10, cloud, Set.of( "login", "device" ) ),
                     webMapping( "other.example.com", 0, IdProviderKey.system(), Set.of( "device" ) ) );
        when( virtualHostService.getVirtualHosts() ).thenReturn( mappings );

        final WebRequest request =
            request( "/.well-known/oauth-protected-resource/server:app", DispatchConstants.MANAGEMENT_CONNECTOR, managementMapping( cloud ) );
        final WebResponse answered = WellKnownDocuments.serve( metadata, request );

        assertThat( answered.getStatus() ).isEqualTo( HttpStatus.OK );
        @SuppressWarnings("unchecked") final Map<String, Object> body = (Map<String, Object>) answered.getBody();
        assertThat( body ).containsEntry( "resource", "https://h/server:app" )
            .containsEntry( "authorization_servers", List.of( "https://admin.example.com/_/idprovider/enonic-cloud" ) );
    }

    @Test
    void a_management_mapping_without_a_web_mapping_issuing_its_tokens_has_nothing_to_describe()
    {
        final IdProviderKey cloud = IdProviderKey.from( "enonic-cloud" );
        final List<VirtualHost> mappings = List.of( webMapping( "site.example.com", 1, cloud, Set.of( "login" ) ) );
        when( virtualHostService.getVirtualHosts() ).thenReturn( mappings );

        final WebRequest request =
            request( "/.well-known/oauth-protected-resource", DispatchConstants.MANAGEMENT_CONNECTOR, managementMapping( cloud ) );
        assertThatThrownBy( () -> WellKnownDocuments.serve( metadata, request ) ).isInstanceOf( WebException.class )
            .extracting( e -> ( (WebException) e ).getStatus() )
            .isEqualTo( HttpStatus.NOT_FOUND );
    }
}
