package com.enonic.xp.core.impl.security.token;

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
import com.enonic.xp.security.IdProviderNotFoundException;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.acl.IdProviderAccessControlList;
import com.enonic.xp.security.acl.IdProviderAccessControlEntry;
import com.enonic.xp.security.acl.IdProviderAccess;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.CreateScopeParams;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.ScopeAlreadyExistsException;
import com.enonic.xp.security.token.UpdateScopeParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * The scope registry against a real node store: where scopes land, who may write them, and what
 * the public reads see.
 */
class ScopeRegistryIntegrationTest
    extends AbstractNodeTest
{
    private static final IdProviderKey SYSTEM = IdProviderKey.system();

    private SecurityServiceImpl service;

    ScopeRegistryIntegrationTest()
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
    void create_stores_the_scope_under_the_id_provider_and_list_reads_it_back_sorted()
    {
        final Scope write = asAdmin( () -> service.createScope( CreateScopeParams.create()
                                                                .idProvider( SYSTEM )
                                                                .id( "content:write" )
                                                                .title( "Edit content" )
                                                                .description( "Create, change and publish content" )
                                                                .roles( PrincipalKeys.from( "role:cms.cm.app", "role:cms.expert" ) )
                                                                .build() ) );
        asAdmin( () -> service.createScope( CreateScopeParams.create().idProvider( SYSTEM ).id( "admin" ).required( true ).build() ) );

        assertThat( write.getTitle() ).isEqualTo( "Edit content" );
        assertThat( write.getDescription() ).isEqualTo( "Create, change and publish content" );
        assertThat( write.getRoles() ).isEqualTo( PrincipalKeys.from( "role:cms.cm.app", "role:cms.expert" ) );
        assertThat( write.isRequired() ).isFalse();

        final Node node = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/scopes/content:write" ) ) );
        assertThat( node.getNodeType() ).isEqualTo( ScopeNodes.SCOPE_TYPE );
        assertThat( node.data().getString( "title" ) ).isEqualTo( "Edit content" );
        assertThat( node.data().getStrings( "roles" ) ).containsExactly( "role:cms.cm.app", "role:cms.expert" );
        final Node folder = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/scopes" ) ) );
        assertThat( folder.getNodeType() ).isEqualTo( ScopeNodes.SCOPES_TYPE );

        assertThat( service.getScopes( SYSTEM ) ).extracting( Scope::getId ).containsExactly( "admin", "content:write" );
        assertThat( service.getScope( SYSTEM, "admin" ) ).hasValueSatisfying( s -> assertThat( s.isRequired() ).isTrue() );
        assertThat( service.getScope( SYSTEM, "admin" ).orElseThrow().getTitle() ).as( "the title falls back to the id" ).isEqualTo( "admin" );
    }

    @Test
    void reads_need_no_permission_and_absent_scopes_read_empty()
    {
        assertThat( service.getScopes( SYSTEM ) ).isEmpty();
        assertThat( service.getScopes( IdProviderKey.from( "nope" ) ) ).isEmpty();
        assertThat( service.getScope( SYSTEM, "x" ) ).isEmpty();
        assertThat( service.getScope( SYSTEM, "not/a/node/name" ) ).isEmpty();

        asAdmin( () -> service.createScope( CreateScopeParams.create().idProvider( SYSTEM ).id( "profile" ).build() ) );

        final Context anonymous = ContextBuilder.create().authInfo( AuthenticationInfo.unAuthenticated() ).build();
        assertThat( anonymous.callWith( () -> service.getScopes( SYSTEM ) ) ).extracting( Scope::getId ).containsExactly( "profile" );
        assertThat( anonymous.callWith( () -> service.getScope( SYSTEM, "profile" ) ) ).isPresent();
    }

    @Test
    void writes_need_the_id_providers_permissions()
    {
        final Context anonymous = ContextBuilder.create().authInfo( AuthenticationInfo.unAuthenticated() ).build();
        final CreateScopeParams params = CreateScopeParams.create().idProvider( SYSTEM ).id( "profile" ).build();

        assertThatThrownBy( () -> anonymous.callWith( () -> service.createScope( params ) ) ).isInstanceOf( NodeAccessException.class );
        assertThat( service.getScopes( SYSTEM ) ).isEmpty();

        asAdmin( () -> service.createScope( params ) );
        assertThat( anonymous.callWith(
            () -> service.updateScope( UpdateScopeParams.create().idProvider( SYSTEM ).id( "profile" ).editor( e -> e.required = true ).build() ) ) ).isEmpty();
        assertThat( anonymous.callWith( () -> service.deleteScope( SYSTEM, "profile" ) ) ).isFalse();
        assertThat( service.getScope( SYSTEM, "profile" ) ).hasValueSatisfying( s -> assertThat( s.isRequired() ).isFalse() );
    }

    @Test
    void duplicates_unknown_id_providers_and_bad_ids_are_refused()
    {
        asAdmin( () -> service.createScope( CreateScopeParams.create().idProvider( SYSTEM ).id( "profile" ).build() ) );

        assertThatThrownBy( () -> asAdmin( () -> service.createScope( CreateScopeParams.create().idProvider( SYSTEM ).id( "profile" ).build() ) ) )
            .isInstanceOf( ScopeAlreadyExistsException.class );
        assertThatThrownBy(
            () -> asAdmin( () -> service.createScope( CreateScopeParams.create().idProvider( IdProviderKey.from( "nope" ) ).id( "profile" ).build() ) ) )
            .isInstanceOf( IdProviderNotFoundException.class );
        assertThatThrownBy( () -> asAdmin( () -> service.createScope( CreateScopeParams.create().idProvider( SYSTEM ).id( "a/b" ).build() ) ) )
            .isInstanceOf( IllegalArgumentException.class );
        assertThat( asAdmin( () -> nodeService.nodeExists( new NodePath( "/identity/nope" ) ) ) ).isFalse();
    }

    @Test
    void update_and_delete()
    {
        asAdmin( () -> service.createScope( CreateScopeParams.create()
                                           .idProvider( SYSTEM )
                                           .id( "content:read" )
                                           .title( "Read content" )
                                           .roles( PrincipalKeys.from( "role:cms.cm.app" ) )
                                           .build() ) );

        final Scope changed = asAdmin( () -> service.updateScope( UpdateScopeParams.create()
                                                                 .idProvider( SYSTEM )
                                                                 .id( "content:read" )
                                                                 .editor( e -> {
                                                                     e.description = "See content";
                                                                     e.roles = PrincipalKeys.from( "role:cms.project.site.viewer" );
                                                                 } )
                                                                 .build() ) ).orElseThrow();
        assertThat( changed.getTitle() ).isEqualTo( "Read content" );
        assertThat( changed.getDescription() ).isEqualTo( "See content" );
        assertThat( changed.getRoles() ).isEqualTo( PrincipalKeys.from( "role:cms.project.site.viewer" ) );
        assertThat( service.getScope( SYSTEM, "content:read" ) ).contains( changed );

        assertThat( asAdmin( () -> service.updateScope( UpdateScopeParams.create().idProvider( SYSTEM ).id( "nope" ).editor( e -> e.required = true ).build() ) ) ).isEmpty();

        assertThat( asAdmin( () -> service.deleteScope( SYSTEM, "content:read" ) ) ).isTrue();
        assertThat( asAdmin( () -> service.deleteScope( SYSTEM, "content:read" ) ) ).isFalse();
        assertThat( asAdmin( () -> service.deleteScope( SYSTEM, "a/b" ) ) ).isFalse();
        assertThat( service.getScopes( SYSTEM ) ).isEmpty();
    }

    @Test
    void the_users_and_groups_folders_are_not_scopes()
    {
        assertThat( service.getScope( SYSTEM, "users" ) ).isEmpty();
        assertThat( asAdmin( () -> service.deleteScope( SYSTEM, "users" ) ) ).isFalse();
        assertThat( asAdmin( () -> nodeService.nodeExists( new NodePath( "/identity/system/users" ) ) ) ).isTrue();
    }

    @Test
    void the_scopes_follow_the_id_providers_permissions()
    {
        final PrincipalKey manager = PrincipalKey.from( "user:system:manager" );
        asAdmin( () -> service.createScope( CreateScopeParams.create().idProvider( SYSTEM ).id( "profile" ).build() ) );
        final Node before = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/scopes/profile" ) ) );
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

        final Node after = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/scopes/profile" ) ) );
        assertThat( after.getPermissions().isAllowedFor( manager, Permission.MODIFY ) ).isTrue();
        final Node folder = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/scopes" ) ) );
        assertThat( folder.getPermissions().isAllowedFor( manager, Permission.CREATE ) ).isTrue();
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
