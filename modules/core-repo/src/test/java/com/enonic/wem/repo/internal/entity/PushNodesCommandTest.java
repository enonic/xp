package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.PushNodesResult;

import static org.junit.Assert.*;

public class PushNodesCommandTest
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

        doPushNodes( NodeIds.from( node.id() ), WS_OTHER );

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

        doPushNodes( NodeIds.from( node.id() ), WS_OTHER );
        doPushNodes( NodeIds.from( child.id() ), WS_OTHER );

        final Node prodNode = CTX_OTHER.callWith( () -> getNodeById( child.id() ) );

        assertTrue( prodNode != null );
    }

    @Test
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

        final PushNodesResult result = doPushNodes( NodeIds.from( child.id() ), WS_OTHER );

        assertEquals( 1, result.getFailed().size() );
        assertEquals( PushNodesResult.Reason.PARENT_NOT_FOUND, result.getFailed().iterator().next().getReason() );
    }

    @Test
    public void ensure_order_for_publish_with_children()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child" ).
            build() );

        final Node child2 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child2" ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child1_1" ).
            build() );

        final Node child2_1 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child2_1" ).
            build() );

        final PushNodesResult result =
            doPushNodes( NodeIds.from( child2_1.id(), child1_1.id(), child1.id(), child2.id(), node.id() ), WS_OTHER );

        assertTrue( result.getFailed().isEmpty() );

        CTX_OTHER.runWith( () -> {
            assertNotNull( getNodeById( node.id() ) );
            assertNotNull( getNodeById( child1.id() ) );
            assertNotNull( getNodeById( child1_1.id() ) );
            assertNotNull( getNodeById( child2.id() ) );
            assertNotNull( getNodeById( child2_1.id() ) );
        } );


    }

}
