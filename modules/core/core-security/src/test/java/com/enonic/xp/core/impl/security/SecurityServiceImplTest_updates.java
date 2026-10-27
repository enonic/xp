package com.enonic.xp.core.impl.security;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * An update reads the entity, applies the change and writes the node. Another writer can get in between the read
 * and the write; the change must then be applied to the node as it is at the write, not to the copy read first,
 * or the other writer's change is lost. The node store is mocked so that a second write slips in right after the read.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityServiceImplTest_updates
{
    private static final IdProviderKey IDP = IdProviderKey.from( "enonic" );

    private static final PrincipalKey USER = PrincipalKey.ofUser( IDP, "alice" );

    private static final PrincipalKey GROUP = PrincipalKey.ofGroup( IDP, "editors" );

    private static final PrincipalKey ROLE = PrincipalKey.ofRole( "cms.admin" );

    @Mock
    private NodeService nodeService;

    @Mock(stubOnly = true)
    private SecurityAuditLogSupport auditLogSupport;

    private SecurityServiceImpl securityService;

    private final Map<NodePath, Node> store = new LinkedHashMap<>();

    private Runnable otherWriter = () -> {
    };

    @BeforeEach
    void setUp()
    {
        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocation -> invocation.getMethod().getDefaultValue() ) );
        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService( new SuPasswordVerifier( "" ) );
        passwordSecurityService.activate( securityConfig );
        securityService = new SecurityServiceImpl( nodeService, auditLogSupport, passwordSecurityService );

        principal( USER, data -> {
            data.setString( PrincipalPropertyNames.LOGIN_KEY, "alice" );
            data.setString( PrincipalPropertyNames.EMAIL_KEY, "alice@example.com" );
            data.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "Alice" );
        } );
        principal( GROUP, data -> {
            data.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "Editors" );
            data.setString( PrincipalPropertyNames.DESCRIPTION_KEY, "Edit content" );
        } );
        principal( ROLE, data -> {
            data.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "Admin" );
            data.setString( PrincipalPropertyNames.DESCRIPTION_KEY, "Administer content" );
        } );
        final PropertyTree idProviderData = new PropertyTree();
        idProviderData.setString( IdProviderPropertyNames.DISPLAY_NAME_KEY, "Enonic" );
        idProviderData.setString( IdProviderPropertyNames.DESCRIPTION_KEY, "Staff" );
        put( Node.create()
                 .id( NodeId.from( "idp" ) )
                 .parentPath( IdProviderNodeTranslator.ID_PROVIDERS_PARENT_PATH )
                 .name( NodeName.from( IDP.toString() ) )
                 .data( idProviderData )
                 .build() );

        // the read returns the node as it is, then the other writer gets its change in before the update
        when( nodeService.getByPath( any( NodePath.class ) ) ).thenAnswer( invocation -> {
            final Node node = store.get( invocation.<NodePath>getArgument( 0 ) );
            final Runnable writer = otherWriter;
            otherWriter = () -> {
            };
            writer.run();
            return node;
        } );
        when( nodeService.update( any( UpdateNodeParams.class ) ) ).thenAnswer( invocation -> {
            final UpdateNodeParams params = invocation.getArgument( 0 );
            final Node current = params.getPath() != null
                ? store.get( params.getPath() )
                : store.values().stream().filter( node -> node.id().equals( params.getId() ) ).findFirst().orElseThrow();
            final EditableNode editable = new EditableNode( current );
            params.getEditor().edit( editable );
            return put( editable.build() );
        } );
        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenReturn( FindNodesByQueryResult.create().build() );
        when( nodeService.getByIds( any( NodeIds.class ) ) ).thenReturn( Nodes.empty() );
    }

    @Test
    void user_update_keeps_a_change_made_after_the_read()
    {
        otherWriter = () -> write( USER.toPath(), data -> data.setString( PrincipalPropertyNames.EMAIL_KEY, "alice@new.example" ) );

        final User updated = securityService.updateUser( UpdateUserParams.create().userKey( USER ).displayName( "Alice Smith" ).build() );

        assertThat( updated.getDisplayName() ).isEqualTo( "Alice Smith" );
        assertThat( updated.getEmail() ).as( "the email the other writer set is kept" ).isEqualTo( "alice@new.example" );
        assertThat( updated.getLogin() ).isEqualTo( "alice" );
    }

    @Test
    void password_keeps_a_change_made_after_the_read()
    {
        otherWriter = () -> write( USER.toPath(), data -> data.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "Alice Smith" ) );

        final User updated = securityService.setPassword( USER, "secret" );

        assertThat( updated.getAuthenticationHash() ).isNotNull();
        assertThat( updated.getDisplayName() ).as( "the display name the other writer set is kept" ).isEqualTo( "Alice Smith" );
    }

    @Test
    void group_update_keeps_a_change_made_after_the_read()
    {
        otherWriter = () -> write( GROUP.toPath(), data -> data.setString( PrincipalPropertyNames.DESCRIPTION_KEY, "Edit everything" ) );

        final Group updated = securityService.updateGroup( UpdateGroupParams.create().groupKey( GROUP ).displayName( "All editors" ).build() );

        assertThat( updated.getDisplayName() ).isEqualTo( "All editors" );
        assertThat( updated.getDescription() ).as( "the description the other writer set is kept" ).isEqualTo( "Edit everything" );
    }

    @Test
    void role_update_keeps_a_change_made_after_the_read()
    {
        otherWriter = () -> write( ROLE.toPath(), data -> data.setString( PrincipalPropertyNames.DESCRIPTION_KEY, "Administer everything" ) );

        final Role updated = securityService.updateRole( UpdateRoleParams.create().roleKey( ROLE ).displayName( "Administrator" ).build() );

        assertThat( updated.getDisplayName() ).isEqualTo( "Administrator" );
        assertThat( updated.getDescription() ).as( "the description the other writer set is kept" ).isEqualTo( "Administer everything" );
    }

    @Test
    void id_provider_update_keeps_a_change_made_after_the_read()
    {
        otherWriter = () -> write( IdProviderNodeTranslator.toIdProviderNodePath( IDP ),
                                   data -> data.setString( IdProviderPropertyNames.DESCRIPTION_KEY, "Staff and partners" ) );

        final IdProvider updated =
            securityService.updateIdProvider( UpdateIdProviderParams.create().key( IDP ).displayName( "Enonic staff" ).build() );

        assertThat( updated.getDisplayName() ).isEqualTo( "Enonic staff" );
        assertThat( updated.getDescription() ).as( "the description the other writer set is kept" ).isEqualTo( "Staff and partners" );
    }

    private void principal( final PrincipalKey key, final Consumer<PropertyTree> fill )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, key.getType().toString() );
        if ( key.getType() != PrincipalType.ROLE )
        {
            data.setString( PrincipalPropertyNames.ID_PROVIDER_KEY, key.getIdProviderKey().toString() );
        }
        fill.accept( data );
        put( Node.create()
                 .id( NodeId.from( key.toString().replace( ':', '-' ) ) )
                 .parentPath( key.toPath().getParentPath() )
                 .name( PrincipalKeyNodeTranslator.toNodeName( key ) )
                 .data( data )
                 .build() );
    }

    private void write( final NodePath path, final Consumer<PropertyTree> change )
    {
        final EditableNode editable = new EditableNode( store.get( path ) );
        change.accept( editable.data );
        put( editable.build() );
    }

    private Node put( final Node node )
    {
        store.put( node.path(), node );
        return node;
    }
}
