package com.enonic.xp.core.impl.security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * The email uniqueness check and the user write that follows it must happen under the id provider's lock,
 * so that two writers in the same id provider cannot both pass the check for the same email. A write that
 * cannot set an email needs neither, and a write that leaves the email as it is needs no check. The editor
 * runs once, on the node as it is at the write. The shared map records whether its lock is held when the
 * node store is called.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityServiceImplTest_idProviderLock
{
    private static final IdProviderKey IDP = IdProviderKey.from( "enonic" );

    private static final PrincipalKey USER = PrincipalKey.ofUser( IDP, "alice" );

    @Mock
    private NodeService nodeService;

    @Mock(stubOnly = true)
    private SecurityAuditLogSupport auditLogSupport;

    private final AtomicBoolean locked = new AtomicBoolean();

    private final List<String> lockedKeys = new ArrayList<>();

    private final List<String> calls = new ArrayList<>();

    private SecurityServiceImpl securityService;

    @BeforeEach
    void setUp()
    {
        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocation -> invocation.getMethod().getDefaultValue() ) );
        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService( new SuPasswordVerifier( "" ) );
        passwordSecurityService.activate( securityConfig );
        securityService = new SecurityServiceImpl( nodeService, auditLogSupport, passwordSecurityService, new RecordingSharedMapService() );

        final PropertyTree data = new PropertyTree();
        data.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, "USER" );
        data.setString( PrincipalPropertyNames.ID_PROVIDER_KEY, IDP.toString() );
        data.setString( PrincipalPropertyNames.LOGIN_KEY, "alice" );
        data.setString( PrincipalPropertyNames.EMAIL_KEY, "alice@example.com" );
        data.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "Alice" );
        final Node alice = Node.create()
            .id( NodeId.from( "alice" ) )
            .parentPath( USER.toPath().getParentPath() )
            .name( PrincipalKeyNodeTranslator.toNodeName( USER ) )
            .data( data )
            .build();

        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenAnswer( invocation -> {
            calls.add( "email check " + ( locked.get() ? "locked" : "unlocked" ) );
            return FindNodesByQueryResult.create().build();
        } );
        when( nodeService.getByIds( any( NodeIds.class ) ) ).thenReturn( Nodes.empty() );
        when( nodeService.getByPath( any( NodePath.class ) ) ).thenReturn( alice );
        when( nodeService.create( any( CreateNodeParams.class ) ) ).thenAnswer( invocation -> {
            calls.add( "create " + ( locked.get() ? "locked" : "unlocked" ) );
            final CreateNodeParams params = invocation.getArgument( 0 );
            return Node.create().id( NodeId.from( "alice" ) ).parentPath( params.getParent() ).name( params.getName() ).data( params.getData() ).build();
        } );
        when( nodeService.update( any( UpdateNodeParams.class ) ) ).thenAnswer( invocation -> {
            final EditableNode editable = new EditableNode( alice );
            invocation.<UpdateNodeParams>getArgument( 0 ).getEditor().edit( editable );
            calls.add( "update " + ( locked.get() ? "locked" : "unlocked" ) );
            return editable.build();
        } );
    }

    @Test
    void create_checks_the_email_and_writes_under_the_id_provider_lock()
    {
        securityService.createUser( CreateUserParams.create().userKey( USER ).login( "alice" ).displayName( "Alice" ).email( "alice@example.com" ).build() );

        assertThat( calls ).containsExactly( "email check locked", "create locked" );
        assertThat( lockedKeys ).containsExactly( IDP.toString() );
        assertThat( locked ).as( "the lock is released afterwards" ).isFalse();
    }

    @Test
    void create_without_an_email_takes_no_lock()
    {
        securityService.createUser( CreateUserParams.create().userKey( USER ).login( "alice" ).displayName( "Alice" ).build() );

        assertThat( calls ).containsExactly( "create unlocked" );
        assertThat( lockedKeys ).isEmpty();
    }

    @Test
    void update_to_another_email_checks_it_and_writes_under_the_id_provider_lock()
    {
        securityService.updateUser( UpdateUserParams.create().userKey( USER ).email( "alice@new.example" ).build() );

        assertThat( calls ).containsExactly( "email check locked", "update locked" );
        assertThat( lockedKeys ).containsExactly( IDP.toString() );
        assertThat( locked ).as( "the lock is released afterwards" ).isFalse();
    }

    @Test
    void update_through_an_editor_runs_it_once_under_the_lock_and_checks_an_email_it_changes()
    {
        final AtomicInteger runs = new AtomicInteger();
        securityService.updateUser( UpdateUserParams.create().userKey( USER ).editor( editable -> {
            runs.incrementAndGet();
            editable.email = "alice@new.example";
        } ).build() );

        assertThat( runs ).hasValue( 1 );
        assertThat( calls ).containsExactly( "email check locked", "update locked" );
        assertThat( lockedKeys ).containsExactly( IDP.toString() );
    }

    @Test
    void update_that_cannot_set_an_email_takes_no_lock()
    {
        securityService.updateUser( UpdateUserParams.create().userKey( USER ).displayName( "Alice Smith" ).build() );

        assertThat( calls ).containsExactly( "update unlocked" );
        assertThat( lockedKeys ).isEmpty();
    }

    @Test
    void update_that_leaves_the_email_as_it_is_checks_nothing()
    {
        securityService.updateUser( UpdateUserParams.create().userKey( USER ).email( "alice@example.com" ).build() );
        securityService.updateUser( UpdateUserParams.create().userKey( USER ).editor( editable -> editable.displayName = "Alice Smith" ).build() );

        assertThat( calls ).containsExactly( "update locked", "update locked" );
    }

    /**
     * Records which keys are locked and whether a lock is held, instead of serializing anything.
     */
    private final class RecordingSharedMapService
        implements SharedMapService
    {
        @Override
        public <K, V> SharedMap<K, V> getSharedMap( final String name )
        {
            assertThat( name ).isEqualTo( SecurityServiceImpl.ID_PROVIDER_LOCKS );
            return new SharedMap<>()
            {
                @Override
                public V get( final K key )
                {
                    return null;
                }

                @Override
                public void delete( final K key )
                {
                }

                @Override
                public void set( final K key, final V value )
                {
                }

                @Override
                public void set( final K key, final V value, final int ttlSeconds )
                {
                }

                @Override
                public V modify( final K key, final Function<V, V> modifier )
                {
                    return modify( key, modifier, -1 );
                }

                @Override
                public V modify( final K key, final Function<V, V> modifier, final int ttlSeconds )
                {
                    lockedKeys.add( key.toString() );
                    locked.set( true );
                    try
                    {
                        return modifier.apply( null );
                    }
                    finally
                    {
                        locked.set( false );
                    }
                }
            };
        }
    }
}
