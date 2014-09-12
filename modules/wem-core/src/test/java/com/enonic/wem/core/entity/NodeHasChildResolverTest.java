package com.enonic.wem.core.entity;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.TestContext;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.query.WorkspaceHasChildrenQuery;

import static org.junit.Assert.*;

public class NodeHasChildResolverTest
{
    final WorkspaceService workspaceService = Mockito.mock( WorkspaceService.class );

    @Test
    public void resolve_node()
        throws Exception
    {
        final Workspace workspace = Workspace.from( "test" );

        final Node node = Node.newNode().
            creator( UserKey.superUser() ).
            id( EntityId.from( "test" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "test" ) ).
            build();

        assertFalse( node.getHasChildren() );

        Mockito.when( workspaceService.hasChildren( Mockito.isA( WorkspaceHasChildrenQuery.class ) ) ).
            thenReturn( true );

        final Node resolvedNode = NodeHasChildResolver.create().
            workspaceService( workspaceService ).
            workspace( workspace ).
            build().
            resolve( node );

        assertTrue( resolvedNode.getHasChildren() );
    }

    @Test
    public void resolve_nodes()
        throws Exception
    {
        final Workspace workspace = Workspace.from( "test" );

        final EntityId nodeWithChildId = EntityId.from( "node-with-child" );
        final Node nodeWithChild = Node.newNode().
            creator( UserKey.superUser() ).
            id( nodeWithChildId ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "node-with-child" ) ).
            build();

        final EntityId nodeWithoutChildId = EntityId.from( "node-without-child" );
        final Node nodeWithoutChild = Node.newNode().
            creator( UserKey.superUser() ).
            id( nodeWithoutChildId ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "node-without-child" ) ).
            build();

        final Nodes nodes = Nodes.from( nodeWithChild, nodeWithoutChild );

        Mockito.when( workspaceService.hasChildren( WorkspaceHasChildrenQuery.create().
            parent( nodeWithChild.path() ).
            workspace( TestContext.TEST_WORKSPACE ).
            repository( TestContext.TEST_REPOSITORY ).
            build() ) ).
            thenReturn( true );
        Mockito.when( workspaceService.hasChildren( WorkspaceHasChildrenQuery.create().
            parent( nodeWithoutChild.path() ).
            workspace( TestContext.TEST_WORKSPACE ).
            repository( TestContext.TEST_REPOSITORY ).
            build() ) ).
            thenReturn( false );

        final Nodes resolvedNodes = NodeHasChildResolver.create().
            workspaceService( workspaceService ).
            workspace( workspace ).
            build().
            resolve( nodes );

        assertTrue( resolvedNodes.getNodeById( nodeWithChildId ).getHasChildren() );
        assertFalse( resolvedNodes.getNodeById( nodeWithoutChildId ).getHasChildren() );
    }

}