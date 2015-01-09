package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.PushNodeException;
import com.enonic.wem.api.workspace.Workspace;

import static org.junit.Assert.*;

public class PushNodeCommandTest
    extends AbstractNodeTest
{
    @Test
    public void push_to_other_workspace()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        Node testWsNode = CTX_OTHER.callWith( () -> getNodeById( node.id() ) );

        assertTrue( testWsNode == null );

        doPushNode( node, WS_PROD );

        testWsNode = CTX_OTHER.callWith( () -> getNodeById( node.id() ) );

        assertTrue( testWsNode != null );
    }

    @Test
    public void push_child()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        final Node child = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child" ).
            build() );

        doPushNode( node, WS_PROD );
        doPushNode( child, WS_PROD );

        final Node prodNode = CTX_OTHER.callWith( () -> getNodeById( child.id() ) );

        assertTrue( prodNode != null );
    }

    @Test(expected = PushNodeException.class)
    public void push_child_fail_if_parent_does_not_exists()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        final Node child = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child" ).
            build() );

        doPushNode( child, WS_PROD );
    }

    private void doPushNode( final Node node, final Workspace wsProd )
    {
        PushNodeCommand.create().
            id( node.id() ).
            target( wsProd ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            build().
            execute();
    }

}
