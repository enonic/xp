package com.enonic.xp.portal.impl.idprovider;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.token.GrantTypes;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OAuthMetadataTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static VirtualHost mapping( final String source, final IdProviderKey defaultIdProvider, final String... flows )
    {
        final VirtualHost mapping = mock( VirtualHost.class );
        when( mapping.getSource() ).thenReturn( source );
        when( mapping.getDefaultIdProviderKey() ).thenReturn( defaultIdProvider );
        when( mapping.getIdProviders() ).thenReturn( defaultIdProvider == null
                                                        ? Map.of()
                                                        : Map.of( defaultIdProvider, flows.length == 0
                                                            ? VirtualHostIdProvider.create().allFlows().build()
                                                            : VirtualHostIdProvider.create().flows( Set.of( flows ) ).build() ) );
        return mapping;
    }

    @Test
    void origin_leaves_out_the_default_port()
    {
        assertThat( OAuthMetadata.origin( "https", "h", 443 ) ).isEqualTo( "https://h" );
        assertThat( OAuthMetadata.origin( "http", "h", 80 ) ).isEqualTo( "http://h" );
        assertThat( OAuthMetadata.origin( "http", "h", 8080 ) ).isEqualTo( "http://h:8080" );
        assertThat( OAuthMetadata.origin( "https", "h", 0 ) ).isEqualTo( "https://h" );
    }

    @Test
    void the_protected_resource_is_the_url_asked_about_and_its_server_the_mappings_default_id_provider()
    {
        final Scopes scopes = Scopes.from( Scope.create().idProvider( IDP ).id( "content:read" ).build() );

        final Map<String, Object> root = OAuthMetadata.protectedResource( "https://h", mapping( "/", IDP ), "/", scopes );
        assertThat( root ).containsEntry( "resource", "https://h" )
            .containsEntry( "authorization_servers", List.of( "https://h/_/idprovider/system" ) )
            .containsEntry( "bearer_methods_supported", List.of( "header" ) )
            .containsEntry( "scopes_supported", List.of( "content:read" ) );

        final Map<String, Object> mcp = OAuthMetadata.protectedResource( "https://h", mapping( "/", IDP ), "/api/mcp", Scopes.empty() );
        assertThat( mcp ).containsEntry( "resource", "https://h/api/mcp" ).doesNotContainKey( "scopes_supported" );

        final Map<String, Object> underAdmin = OAuthMetadata.protectedResource( "https://h", mapping( "/admin", IDP ), "/admin/tool", Scopes.empty() );
        assertThat( underAdmin ).containsEntry( "resource", "https://h/admin/tool" )
            .containsEntry( "authorization_servers", List.of( "https://h/admin/_/idprovider/system" ) );

        assertThat( OAuthMetadata.protectedResource( "https://h", mapping( "/", null ), "/", Scopes.empty() ) ).as( "nothing to authorize with" ).isNull();
    }

    @Test
    void the_authorization_server_lists_the_endpoints_of_the_enabled_flows()
    {
        final Map<String, Object> all = OAuthMetadata.authorizationServer( "https://h", mapping( "/", IDP ), IDP, Scopes.empty() );
        assertThat( all ).containsEntry( "issuer", "https://h/_/idprovider/system" )
            .containsEntry( "authorization_endpoint", "https://h/_/idprovider/system/authorize" )
            .containsEntry( "device_authorization_endpoint", "https://h/_/idprovider/system/device/code" )
            .containsEntry( "token_endpoint", "https://h/_/idprovider/system/token" )
            .containsEntry( "registration_endpoint", "https://h/_/idprovider/system/register" )
            .containsEntry( "response_types_supported", List.of( "code" ) )
            .containsEntry( "grant_types_supported", List.of( GrantTypes.AUTHORIZATION_CODE, GrantTypes.DEVICE_CODE, GrantTypes.REFRESH_TOKEN ) )
            .containsEntry( "token_endpoint_auth_methods_supported", List.of( "none" ) )
            .containsEntry( "code_challenge_methods_supported", List.of( "S256" ) )
            .doesNotContainKey( "scopes_supported" );

        final Map<String, Object> deviceOnly =
            OAuthMetadata.authorizationServer( "https://h", mapping( "/admin", IDP, IdProviderFlow.LOGIN, IdProviderFlow.DEVICE ), IDP, Scopes.empty() );
        assertThat( deviceOnly ).containsEntry( "issuer", "https://h/admin/_/idprovider/system" )
            .doesNotContainKey( "authorization_endpoint" )
            .containsEntry( "device_authorization_endpoint", "https://h/admin/_/idprovider/system/device/code" )
            .containsEntry( "token_endpoint", "https://h/admin/_/idprovider/system/token" )
            .doesNotContainKey( "registration_endpoint" )
            .containsEntry( "response_types_supported", List.of() )
            .containsEntry( "grant_types_supported", List.of( GrantTypes.DEVICE_CODE, GrantTypes.REFRESH_TOKEN ) );

        final Map<String, Object> registering = OAuthMetadata.authorizationServer(
            "https://h", mapping( "/", IDP, IdProviderFlow.CODE, IdProviderFlow.REGISTER ), IDP, Scopes.empty() );
        assertThat( registering ).containsEntry( "registration_endpoint", "https://h/_/idprovider/system/register" )
            .doesNotContainKey( "device_authorization_endpoint" );

        final Map<String, Object> none = OAuthMetadata.authorizationServer( "https://h", mapping( "/", IDP, IdProviderFlow.LOGIN ), IDP, Scopes.empty() );
        assertThat( none ).doesNotContainKey( "token_endpoint" ).containsEntry( "grant_types_supported", List.of() );
    }

    @Test
    void the_id_provider_of_an_authorization_server_path()
    {
        assertThat( OAuthMetadata.idProviderOf( mapping( "/", IDP ), "/_/idprovider/system" ) ).isEqualTo( IDP );
        assertThat( OAuthMetadata.idProviderOf( mapping( "/admin", IDP ), "/admin/_/idprovider/system" ) ).isEqualTo( IDP );
        assertThat( OAuthMetadata.idProviderOf( mapping( "/admin", IDP ), "/_/idprovider/system" ) ).isNull();
        assertThat( OAuthMetadata.idProviderOf( mapping( "/", IDP ), "/_/idprovider/other" ) ).as( "not enabled" ).isNull();
        assertThat( OAuthMetadata.idProviderOf( mapping( "/", IDP ), "/_/idprovider/system/token" ) ).isNull();
        assertThat( OAuthMetadata.idProviderOf( mapping( "/", IDP ), "/_/idprovider/" ) ).isNull();
        assertThat( OAuthMetadata.idProviderOf( mapping( "/", IDP ), "/api/mcp" ) ).isNull();
    }

    @Test
    void the_resource_metadata_url()
    {
        assertThat( OAuthMetadata.resourceMetadataUrl( "https://h", "/" ) ).isEqualTo( "https://h/.well-known/oauth-protected-resource" );
        assertThat( OAuthMetadata.resourceMetadataUrl( "https://h", "/api/mcp" ) ).isEqualTo( "https://h/.well-known/oauth-protected-resource/api/mcp" );
    }
}
