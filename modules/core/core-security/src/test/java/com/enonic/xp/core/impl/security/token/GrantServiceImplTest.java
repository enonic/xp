package com.enonic.xp.core.impl.security.token;

import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.security.InMemorySharedMapService;
import com.enonic.xp.core.impl.security.token.GrantNodes.GrantNode;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.UpdateGrantParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Configuration handling and the write path; the behaviour against the node store is covered by the
 * integration test of the same name.
 */
class GrantServiceImplTest
{
    private static final PrincipalKey USER = PrincipalKey.ofUser( IdProviderKey.from( "enonic" ), "alice" );

    private final NodeService nodeService = mock( NodeService.class );

    private final GrantServiceImpl service = new GrantServiceImpl( nodeService, new InMemorySharedMapService(), Clock.systemUTC() );

    @Test
    void defaults_without_activation()
    {
        service.activate( Map.of() );
        verifyNoInteractions( nodeService );
    }

    @Test
    void invalid_settings_are_rejected()
    {
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.ttl", "PT0S" ) ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.maxPerUser", "0" ) ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.rotateAfter", "-PT1S" ) ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.reuseGrace", "soon" ) ) ).isInstanceOf( DateTimeParseException.class );
        assertThatThrownBy( () -> service.activate( Map.of( "token.grant.maxPerClient", "x" ) ) ).isInstanceOf( NumberFormatException.class );
    }

    /**
     * The update reads the grant and then writes it. Here another writer changes the audience in
     * between; the update must apply its change to the grant as it is at the write, so that change is kept.
     */
    @Test
    void update_keeps_a_change_made_after_the_read()
    {
        service.activate( Map.of() );
        final Instant now = Instant.now();
        final GrantNode stored =
            new GrantNode( "g1", USER, "cli", Set.of( "api" ), Set.of( "read" ), RoleRestriction.none(), now, now, now.plusSeconds( 3600 ),
                           "hash", new byte[32], 0, null );
        when( nodeService.getById( NodeId.from( "g1" ) ) ).thenReturn( node( stored ) );
        when( nodeService.update( any( UpdateNodeParams.class ) ) ).thenAnswer( invocation -> {
            final EditableNode editable = new EditableNode( node( stored.changed( stored.scope(), Set.of( "api", "admin" ), stored.restriction() ) ) );
            invocation.<UpdateNodeParams>getArgument( 0 ).getEditor().edit( editable );
            return editable.build();
        } );

        final Optional<Grant> updated =
            service.update( UpdateGrantParams.create().subject( USER ).grantId( "g1" ).scope( Set.of( "read", "write" ) ).build() );

        assertThat( updated ).isPresent();
        assertThat( updated.get().getScope() ).containsExactlyInAnyOrder( "read", "write" );
        assertThat( updated.get().getAudience() ).as( "the audience the other writer set is kept" ).containsExactlyInAnyOrder( "api", "admin" );
    }

    private static Node node( final GrantNode grant )
    {
        return Node.create()
            .id( NodeId.from( grant.id() ) )
            .name( grant.id() )
            .parentPath( GrantNodes.grantsPath( grant.subject() ) )
            .nodeType( GrantNodes.GRANT_TYPE )
            .data( grant.toData() )
            .build();
    }
}
