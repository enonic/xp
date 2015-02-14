package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
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
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node2" ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child1" ).
            build() );

        final Node child2 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child2" ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            parent( child1.path() ).
            name( "my-child1_1" ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            parent( child1_1.path() ).
            name( "my-child1_1" ).
            build() );

        final Node child2_1 = createNode( CreateNodeParams.create().
            parent( child2.path() ).
            name( "my-child2_1" ).
            build() );

        pushNodes( NodeIds.from( node.id(), node2.id(), child1.id(), child1_1.id(), child1_1_1.id(), child2.id(), child2_1.id() ),
                   WS_OTHER );

        assertNotNull( getNodeByPathInOther( NodePath.newNodePath( node.path(), child1.name().toString() ).build() ) );

        final Node movedNode = MoveNodeCommand.create().
            id( node.id() ).
            newParent( node2.path() ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            build().
            execute();

        pushNodes( NodeIds.from( node.id() ), WS_OTHER );

        assertNotNull( getNodeByPathInOther( NodePath.newNodePath( movedNode.path(), child1.name().toString() ).build() ) );
        assertNull( getNodeByPathInOther( NodePath.newNodePath( node.path(), child1.name().toString() ).build() ) );
    }

    @Test
    public void push_after_rename()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node2" ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child1" ).
            build() );

        final Node child2 = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child2" ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            parent( child1.path() ).
            name( "my-child1_1" ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            parent( child1_1.path() ).
            name( "my-child1_1" ).
            build() );

        final Node child2_1 = createNode( CreateNodeParams.create().
            parent( child2.path() ).
            name( "my-child2_1" ).
            build() );

        pushNodes( NodeIds.from( node.id(), node2.id(), child1.id(), child1_1.id(), child1_1_1.id(), child2.id(), child2_1.id() ),
                   WS_OTHER );

        renameNode( node2 );
        renameNode( node );
        renameNode( child1 );
        renameNode( child2 );
        renameNode( child2_1 );
        renameNode( child1_1_1 );

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
            versionService( this.versionService ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            build().
            execute();
    }

    private Node getNodeByPathInOther( final NodePath nodePath )
    {
        System.out.println( nodePath.toString() );
        return CTX_OTHER.callWith( () -> getNodeByPath( nodePath ) );
    }


}
