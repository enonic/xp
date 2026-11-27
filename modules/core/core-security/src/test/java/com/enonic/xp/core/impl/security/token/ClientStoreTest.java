package com.enonic.xp.core.impl.security.token;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.IdProviderKey;
import java.util.Set;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.UpdateClientParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientStoreTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static Node node( final Client scope )
    {
        return Node.create()
            .id( NodeId.from( "client-node" ) )
            .name( RegistryNodeNames.of( scope.getId() ) )
            .parentPath( ClientNodes.clientsPath( IDP ) )
            .nodeType( ClientNodes.CLIENT_TYPE )
            .data( ClientNodes.toData( scope ) )
            .build();
    }

    @Test
    void an_update_edits_the_client_as_stored_at_the_update_not_as_read_before_it()
    {
        final Client read = Client.create().idProvider( IDP ).id( "cli" ).displayName( "CLI" ).scope( Set.of( "a" ) ).build();
        final Client meanwhile = Client.create().idProvider( IDP ).id( "cli" ).displayName( "CLI" ).scope( Set.of( "b" ) ).build();
        final NodeService nodeService = mock( NodeService.class );
        when( nodeService.getByPath( any( NodePath.class ) ) ).thenReturn( node( read ) );
        when( nodeService.update( any( UpdateNodeParams.class ) ) ).thenAnswer( i -> {
            final EditableNode editable = new EditableNode( node( meanwhile ) );
            i.<UpdateNodeParams>getArgument( 0 ).getEditor().edit( editable );
            return Node.create( node( meanwhile ) ).data( editable.data ).build();
        } );

        final Client changed = new ClientStore( nodeService ).update(
            UpdateClientParams.create().idProvider( IDP ).id( "cli" ).editor( e -> e.displayName = "Enonic CLI" ).build() ).orElseThrow();

        assertThat( changed.getDisplayName() ).isEqualTo( "Enonic CLI" );
        assertThat( changed.getScope() ).as( "the scope changed meanwhile is kept" ).containsExactly( "b" );
    }
}
