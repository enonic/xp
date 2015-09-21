package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodesResult;

import static org.junit.Assert.*;

public class PushNodesCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void push_to_other_branch()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        Node testWsNode = CTX_OTHER.callWith( () -> getNodeById( node.id() ) );

        assertTrue( testWsNode == null );

        pushNodes( NodeIds.from( node.id() ), WS_OTHER );

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

        pushNodes( NodeIds.from( node.id() ), WS_OTHER );
        pushNodes( NodeIds.from( child.id() ), WS_OTHER );

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

        final PushNodesResult result = pushNodes( NodeIds.from( child.id() ), WS_OTHER );

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
            pushNodes( NodeIds.from( child2_1.id(), child1_1.id(), child1.id(), child2.id(), node.id() ), WS_OTHER );

        assertTrue( result.getFailed().isEmpty() );

        CTX_OTHER.runWith( () -> {
            assertNotNull( getNodeById( node.id() ) );
            assertNotNull( getNodeById( child1.id() ) );
            assertNotNull( getNodeById( child1_1.id() ) );
            assertNotNull( getNodeById( child2.id() ) );
            assertNotNull( getNodeById( child2_1.id() ) );
        } );
    }

    @Test
    public void moved_nodes_yields_reindex_of_children()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            setNodeId( NodeId.from( "node1" ) ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node2" ).
            setNodeId( NodeId.from( "node2" ) ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            parent( node1.path() ).
            name( "child1" ).
            setNodeId( NodeId.from( "child1" ) ).
            build() );

        final Node child2 = createNode( CreateNodeParams.create().
            parent( node1.path() ).
            name( "child2" ).
            setNodeId( NodeId.from( "child2" ) ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            parent( child1.path() ).
            name( "child1_1" ).
            setNodeId( NodeId.from( "child1_1" ) ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            parent( child1_1.path() ).
            name( "child1_1_1" ).
            setNodeId( NodeId.from( "child1_1_1" ) ).
            build() );

        final Node child2_1 = createNode( CreateNodeParams.create().
            parent( child2.path() ).
            name( "child2_1" ).
            setNodeId( NodeId.from( "child2_1" ) ).
            build() );

        pushNodes( NodeIds.from( node1.id(), node2.id(), child1.id(), child1_1.id(), child1_1_1.id(), child2.id(), child2_1.id() ),
                   WS_OTHER );

        assertNotNull( getNodeByPathInOther( NodePath.create( node1.path(), child1.name().toString() ).build() ) );

        final Node movedNode = MoveNodeCommand.create().
            id( node1.id() ).
            newParent( node2.path() ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        pushNodes( NodeIds.from( node1.id() ), WS_OTHER );

        assertNotNull( getNodeByPathInOther( NodePath.create( movedNode.path(), child1.name().toString() ).build() ) );

        assertNull( getNodeByPathInOther( NodePath.create( node1.path(), child1.name().toString() ).build() ) );

        Node child1Node = CTX_OTHER.callWith( () -> getNodeById( child1.id() ) );
        assertNotNull( getNodeByPathInOther( NodePath.create( child1Node.path(), child1_1.name().toString() ).build() ) );
    }

    @Test
    public void push_rename_push()
        throws Exception
    {
        final Node parent = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "parent" ).
            setNodeId( NodeId.from( "parent" ) ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            parent( parent.path() ).
            name( "child1" ).
            setNodeId( NodeId.from( "child1" ) ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            parent( child1.path() ).
            name( "child1_1" ).
            setNodeId( NodeId.from( "child1_1" ) ).
            build() );

        pushNodes( NodeIds.from( parent.id(), child1.id() ), WS_OTHER );

        renameNode( parent );
        renameNode( child1 );
        renameNode( child1_1 );

        final PushNodesResult result = pushNodes( NodeIds.from( parent.id(), child1.id() ), WS_OTHER );

        assertEquals( 2, result.getSuccessfull().getSize() );
    }

    @Test
    public void push_after_rename()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            setNodeId( NodeId.from( "node1" ) ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "child1" ).
            setNodeId( NodeId.from( "child1" ) ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            parent( child1.path() ).
            name( "child1_1" ).
            setNodeId( NodeId.from( "child1_1" ) ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            parent( child1_1.path() ).
            name( "child1_1_1" ).
            setNodeId( NodeId.from( "child1_1_1" ) ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node2" ).
            setNodeId( NodeId.from( "node2" ) ).
            build() );

        final Node child2 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "child2" ).
            setNodeId( NodeId.from( "child2" ) ).
            build() );

        final Node child2_1 = createNode( CreateNodeParams.create().
            parent( child2.path() ).
            name( "child2_1" ).
            setNodeId( NodeId.from( "child2_1" ) ).
            build() );

        pushNodes( NodeIds.from( node.id(), node2.id(), child1.id(), child1_1.id(), child1_1_1.id(), child2.id(), child2_1.id() ),
                   WS_OTHER );

        renameNode( node );
        renameNode( child1 );
        renameNode( child1_1 );
        renameNode( child1_1_1 );
        renameNode( node2 );
        renameNode( child2 );
        renameNode( child2_1 );

        final PushNodesResult result =
            pushNodes( NodeIds.from( child1_1_1.id(), child1_1.id(), node.id(), child2_1.id(), node2.id(), child1.id(), child2.id() ),
                       WS_OTHER );

        assertEquals( 7, result.getSuccessfull().getSize() );
    }


    private void renameNode( final Node node )
    {
        MoveNodeCommand.create().
            id( node.id() ).
            newNodeName( NodeName.from( node.id().toString() + "edited" ) ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private Node getNodeByPathInOther( final NodePath nodePath )
    {
        return CTX_OTHER.callWith( () -> getNodeByPath( nodePath ) );
    }


}
