package com.enonic.xp.core.impl.security.token;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.UpdateScopeParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScopeStoreTest
{
    private static final IdProviderKey IDP = IdProviderKey.system();

    private static Node node( final Scope scope )
    {
        return Node.create()
            .id( NodeId.from( "scope-node" ) )
            .name( scope.getId() )
            .parentPath( ScopeNodes.scopesPath( IDP ) )
            .nodeType( ScopeNodes.SCOPE_TYPE )
            .data( ScopeNodes.toData( scope ) )
            .build();
    }

    @Test
    void an_update_edits_the_scope_as_stored_at_the_update_not_as_read_before_it()
    {
        final Scope read = Scope.create().idProvider( IDP ).id( "content:read" ).title( "Read" ).roles( PrincipalKeys.from( "role:a" ) ).build();
        final Scope meanwhile = Scope.create().idProvider( IDP ).id( "content:read" ).title( "Read" ).roles( PrincipalKeys.from( "role:b" ) ).build();
        final NodeService nodeService = mock( NodeService.class );
        when( nodeService.getByPath( any( NodePath.class ) ) ).thenReturn( node( read ) );
        when( nodeService.update( any( UpdateNodeParams.class ) ) ).thenAnswer( i -> {
            final EditableNode editable = new EditableNode( node( meanwhile ) );
            i.<UpdateNodeParams>getArgument( 0 ).getEditor().edit( editable );
            return Node.create( node( meanwhile ) ).data( editable.data ).build();
        } );

        final Scope changed = new ScopeStore( nodeService ).update(
            UpdateScopeParams.create().idProvider( IDP ).id( "content:read" ).editor( e -> e.title = "See content" ).build() ).orElseThrow();

        assertThat( changed.getTitle() ).isEqualTo( "See content" );
        assertThat( changed.getRoles() ).as( "the roles changed meanwhile are kept" ).isEqualTo( PrincipalKeys.from( "role:b" ) );
    }
}
