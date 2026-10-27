package com.enonic.xp.core.impl.security.token;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.security.PasswordSecurityService;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupport;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.impl.shared.LocalSharedMapService;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.acl.IdProviderAccessControlList;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;
import com.enonic.xp.security.acl.IdProviderAccess;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.IdProviderNotFoundException;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.ClientAlreadyExistsException;
import com.enonic.xp.security.token.CreateClientParams;
import com.enonic.xp.security.token.GrantTypes;
import com.enonic.xp.security.token.UpdateClientParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * The client registry against a real node store: where clients land, who may write them, what a
 * fresh installation gets, and what cannot be deleted.
 */
class ClientRegistryIntegrationTest
    extends AbstractNodeTest
{
    private static final IdProviderKey SYSTEM = IdProviderKey.system();

    private SecurityServiceImpl service;

    ClientRegistryIntegrationTest()
    {
        super( true );
    }

    @BeforeEach
    void setUp()
    {
        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocation -> invocation.getMethod().getDefaultValue() ) );
        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService();
        passwordSecurityService.activate( securityConfig );
        final SecurityServiceImpl securityService =
            new SecurityServiceImpl( nodeService, mock( SecurityAuditLogSupport.class, withSettings().stubOnly() ), passwordSecurityService,
                                     new LocalSharedMapService() );
        asAdmin( () -> {
            SecurityInitializer.create()
                .setIndexService( indexService )
                .setSecurityService( securityService )
                .setNodeService( nodeService )
                .build()
                .initialize();
            return null;
        } );
        service = securityService;
    }

    @Test
    void a_fresh_installation_has_the_cli_registered_with_the_system_id_provider()
    {
        final Client cli = service.getClient( SYSTEM, "enonic-cli" ).orElseThrow();

        assertThat( cli.getDisplayName() ).isEqualTo( "Enonic CLI" );
        assertThat( cli.getGrantTypes() ).containsExactlyInAnyOrder( GrantTypes.DEVICE_CODE, GrantTypes.REFRESH_TOKEN );
        assertThat( cli.getScope() ).as( "no cap" ).isEmpty();
        assertThat( cli.getResource() ).isEmpty();
        assertThat( cli.isEnabled() ).isTrue();
        assertThat( service.getClients( SYSTEM ) ).extracting( Client::getId ).containsExactly( "enonic-cli" );
        final Node node = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/clients/mvxg63tjmmwwg3dj" ) ) );
        assertThat( node.getNodeType() ).isEqualTo( ClientNodes.CLIENT_TYPE );
    }

    @Test
    void the_enonic_cli_can_be_changed_and_disabled_but_not_removed()
    {
        final Client disabled = asAdmin( () -> service.updateClient( UpdateClientParams.create()
                                                                   .idProvider( SYSTEM )
                                                                   .id( "enonic-cli" )
                                                                   .editor( e -> {
                                                                       e.enabled = false;
                                                                       e.scope = Set.of( "content:read" );
                                                                   } )
                                                                   .build() ) ).orElseThrow();
        assertThat( disabled.isEnabled() ).isFalse();
        assertThat( disabled.getScope() ).containsExactly( "content:read" );

        assertThatThrownBy( () -> asAdmin( () -> service.deleteClient( SYSTEM, "enonic-cli" ) ) ).isInstanceOf( IllegalArgumentException.class );
        assertThat( service.getClient( SYSTEM, "enonic-cli" ) ).isPresent();
    }

    @Test
    void create_stores_every_value_and_reads_it_back()
    {
        final Client created = asAdmin( () -> service.createClient( CreateClientParams.create()
                                                                   .idProvider( SYSTEM )
                                                                   .id( "claude" )
                                                                   .displayName( "Claude" )
                                                                   .uri( "https://claude.ai" )
                                                                   .logoUri( "https://claude.ai/favicon.ico" )
                                                                   .policyUri( "https://anthropic.com/privacy" )
                                                                   .tosUri( "https://anthropic.com/terms" )
                                                                   .grantTypes( List.of( GrantTypes.AUTHORIZATION_CODE, GrantTypes.REFRESH_TOKEN ) )
                                                                   .redirectUris( List.of( "https://claude.ai/api/mcp/auth_callback" ) )
                                                                   .scope( List.of( "content:read" ) )
                                                                   .resource( List.of( "https://example.com/api/mcp" ) )
                                                                   .grantTtl( Duration.ofDays( 30 ) )
                                                                   .build() ) );

        assertThat( service.getClient( SYSTEM, "claude" ) ).contains( created );
        assertThat( created.getLogoUri() ).isEqualTo( "https://claude.ai/favicon.ico" );
        assertThat( created.getRedirectUris() ).containsExactly( "https://claude.ai/api/mcp/auth_callback" );
        assertThat( created.getGrantTtl() ).isEqualTo( Duration.ofDays( 30 ) );
        assertThat( service.getClients( SYSTEM ) ).extracting( Client::getId ).containsExactly( "claude", "enonic-cli" );

        assertThat( asAdmin( () -> service.deleteClient( SYSTEM, "claude" ) ) ).isTrue();
        assertThat( service.getClient( SYSTEM, "claude" ) ).isEmpty();
    }

    @Test
    void reads_need_no_permission_writes_need_the_id_providers()
    {
        final Context anonymous = ContextBuilder.create().authInfo( AuthenticationInfo.unAuthenticated() ).build();

        assertThat( anonymous.callWith( () -> service.getClient( SYSTEM, "enonic-cli" ) ) ).isPresent();
        assertThat( anonymous.callWith( () -> service.getClients( SYSTEM ) ) ).hasSize( 1 );
        assertThat( anonymous.callWith( () -> service.getClient( IdProviderKey.from( "nope" ), "enonic-cli" ) ) ).isEmpty();
        assertThat( anonymous.callWith( () -> service.getClient( SYSTEM, "not/a/name" ) ) ).isEmpty();

        final CreateClientParams params = CreateClientParams.create().idProvider( SYSTEM ).id( "claude" ).build();
        assertThatThrownBy( () -> anonymous.callWith( () -> service.createClient( params ) ) ).isInstanceOf( NodeAccessException.class );
        assertThat( anonymous.callWith(
            () -> service.updateClient( UpdateClientParams.create().idProvider( SYSTEM ).id( "enonic-cli" ).editor( e -> e.enabled = false ).build() ) ) ).isEmpty();
        assertThat( service.getClient( SYSTEM, "enonic-cli" ).orElseThrow().isEnabled() ).isTrue();
    }

    @Test
    void duplicates_unknown_id_providers_and_bad_ids_are_refused()
    {
        assertThatThrownBy( () -> asAdmin( () -> service.createClient( CreateClientParams.create().idProvider( SYSTEM ).id( "enonic-cli" ).build() ) ) )
            .isInstanceOf( ClientAlreadyExistsException.class );
        assertThatThrownBy(
            () -> asAdmin( () -> service.createClient( CreateClientParams.create().idProvider( IdProviderKey.from( "nope" ) ).id( "x" ).build() ) ) )
            .isInstanceOf( IdProviderNotFoundException.class );
        assertThat( asAdmin( () -> service.updateClient( UpdateClientParams.create().idProvider( SYSTEM ).id( "a/b" ).editor( e -> e.enabled = false ).build() ) ) )
            .isEmpty();
        assertThat( asAdmin( () -> service.deleteClient( SYSTEM, "users" ) ) ).isFalse();
    }

    @Test
    void ids_a_node_name_cannot_hold_and_ids_differing_in_case_alone_are_stored_apart()
    {
        for ( String id : List.of( "a/b", "Cli", "cli" ) )
        {
            asAdmin( () -> service.createClient( CreateClientParams.create().idProvider( SYSTEM ).id( id ).displayName( id ).build() ) );
        }
        assertThat( service.getClient( SYSTEM, "a/b" ).orElseThrow().getDisplayName() ).isEqualTo( "a/b" );
        assertThat( service.getClient( SYSTEM, "Cli" ).orElseThrow().getDisplayName() ).isEqualTo( "Cli" );
        assertThat( service.getClient( SYSTEM, "cli" ).orElseThrow().getDisplayName() ).isEqualTo( "cli" );
        assertThat( service.getClient( SYSTEM, "CLI" ) ).isEmpty();
        assertThat( asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/clients/mexwe" ) ) ).data().getString( "id" ) ).isEqualTo( "a/b" );
        assertThat( asAdmin( () -> service.deleteClient( SYSTEM, "Cli" ) ) ).isTrue();
        assertThat( service.getClients( SYSTEM ) ).extracting( Client::getId ).contains( "a/b", "cli", "enonic-cli" ).doesNotContain( "Cli" );
    }

    @Test
    void the_clients_follow_the_id_providers_permissions()
    {
        final PrincipalKey manager = PrincipalKey.from( "user:system:manager" );
        final Node before = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/clients/mvxg63tjmmwwg3dj" ) ) );
        assertThat( before.getPermissions().isAllowedFor( manager, Permission.MODIFY ) ).isFalse();

        asAdmin( () -> service.updateIdProvider( UpdateIdProviderParams.create()
                                                     .key( SYSTEM )
                                                     .permissions( IdProviderAccessControlList.create()
                                                                       .add( IdProviderAccessControlEntry.create()
                                                                                 .principal( manager )
                                                                                 .access( IdProviderAccess.ADMINISTRATOR )
                                                                                 .build() )
                                                                       .build() )
                                                     .build() ) );

        final Node after = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/clients/mvxg63tjmmwwg3dj" ) ) );
        assertThat( after.getPermissions().isAllowedFor( manager, Permission.MODIFY ) ).isTrue();
    }

    private static <T> T asAdmin( final Callable<T> body )
    {
        final Context context = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SecurityConstants.BRANCH_SECURITY )
            .build();
        return context.callWith( body );
    }
}
