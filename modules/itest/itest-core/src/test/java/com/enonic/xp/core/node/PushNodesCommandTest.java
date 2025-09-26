package com.enonic.xp.core.node;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.PushNodeResult;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.FindNodesByQueryCommand;
import com.enonic.xp.repo.impl.node.PushNodesCommand;
import com.enonic.xp.repo.impl.node.ResolveSyncWorkCommand;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PushNodesCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
        ctxOther().callWith( this::createDefaultRootNode );
    }

    @Test
    void push_single()
    {
        final Node node = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node" ).build() );

        Node testWsNode = ctxOther().callWith( () -> getNodeById( node.id() ) );

        assertNull( testWsNode );

        pushNodes( WS_OTHER, node.id() );

        testWsNode = ctxOther().callWith( () -> getNodeById( node.id() ) );

        assertThat( testWsNode ).extracting( Node::id ).isEqualTo( node.id() );
    }

    @Test
    void push_child()
    {
        final Node node = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node" ).build() );

        final Node child = createNode( CreateNodeParams.create().parent( node.path() ).name( "my-child" ).build() );

        pushNodes( WS_OTHER, node.id() );
        pushNodes( WS_OTHER, child.id() );

        final Node prodNode = ctxOther().callWith( () -> getNodeById( child.id() ) );

        assertThat( prodNode ).extracting( Node::id ).isEqualTo( child.id() );
    }

    @Test
    void only_selected_node_pushed()
    {
        final Node node = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node" ).build() );

        createNode( CreateNodeParams.create().parent( node.path() ).name( "my-child" ).build() );

        createNode( CreateNodeParams.create().parent( node.path() ).name( "my-child2" ).build() );

        final PushNodesResult result = pushNodes( WS_OTHER, node.id() );
        assertThat( result.getSuccessful() ).extracting( PushNodeResult::getNodeId ).containsExactly( node.id() );

        final FindNodesByQueryResult allNodesInOther = ctxOther().callWith( () -> FindNodesByQueryCommand.create()
            .query( NodeQuery.create().parent( NodePath.ROOT ).build() )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute() );

        assertThat( allNodesInOther.getNodeHits().getNodeIds() ).containsExactly( node.id() );
    }

    @Test
    public void push_child_missing_permission()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        final Node child = createNode( CreateNodeParams.create().
            parent( node.path() ).
            name( "my-child" ).permissions( AccessControlList.create()
                                                .add( AccessControlEntry.create()
                                                          .allowAll()
                                                          .deny( Permission.PUBLISH )
                                                          .principal( TEST_DEFAULT_USER.getKey() )
                                                          .build() )
                                                .build() ).build() );

        final PushNodesResult result = pushNodes( WS_OTHER, node.id(), child.id() );

        assertEquals( 1, result.getSuccessful().size() );
        assertEquals( 1, result.getFailed().size() );
        assertEquals( PushNodeResult.Reason.ACCESS_DENIED, result.getFailed().iterator().next().getFailureReason() );
    }

    @Test
    public void push_fail_if_node_already_exists()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).name( "my-node" ).build() );

        ctxOther().callWith( () -> createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node" ).build() ) );

        final PushNodesResult result = pushNodes( WS_OTHER, node.id() );

        assertEquals( 1, result.getFailed().size() );
        assertEquals( 0, result.getSuccessful().size() );
        assertEquals( PushNodeResult.Reason.ALREADY_EXIST, result.getFailed().iterator().next().getFailureReason() );
    }


    @Test
    public void push_rename_push_test()
        throws Exception
    {
        PushNodesResult result;

        //Creates and pushes a content
        final Node node = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node" ).build() );
        result = pushNodes( WS_OTHER, node.id() );
        assertEquals( 1, result.getSuccessful().size() );
        assertNotNull( getNodeByPath( new NodePath( "/my-node" ) ) );
        assertNotNull( getNodeByPathInOther( new NodePath( "/my-node" ) ) );

        //Renames the content
        renameNode( node.id(), "my-node-renamed" );
        nodeService.refresh( RefreshMode.ALL );

        assertNull( getNodeByPath( new NodePath( "/my-node" ) ) );
        assertNotNull( getNodeByPath( new NodePath( "/my-node-renamed" ) ) );
        assertNotNull( getNodeByPathInOther( new NodePath( "/my-node" ) ) );

        //Pushed the renames content

        result = pushNodes( WS_OTHER, node.id() );
        assertEquals( 1, result.getSuccessful().size() );
        assertNotNull( getNodeByPathInOther( new NodePath( "/my-node-renamed" ) ) );
        assertNull( getNodeByPathInOther( new NodePath( "/my-node" ) ) );
    }

    @Test
    public void push_child_fail_if_parent_does_not_exists()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).name( "my-node" ).build() );

        final Node child = createNode( CreateNodeParams.create().parent( node.path() ).name( "my-child" ).build() );

        final PushNodesResult result = pushNodes( WS_OTHER, child.id() );

        assertEquals( 1, result.getFailed().size() );
        assertEquals( PushNodeResult.Reason.PARENT_NOT_FOUND, result.getFailed().iterator().next().getFailureReason() );
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

        final PushNodesResult result = pushNodes( WS_OTHER, child2_1.id(), child1_1.id(), child1.id(), child2.id(), node.id() );

        assertTrue( result.getFailed().isEmpty() );

        ctxOther().runWith( () -> {
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
        final Node node1 =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "node1" ).setNodeId( NodeId.from( "node1" ) ).build() );

        final Node node2 =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "node2" ).setNodeId( NodeId.from( "node2" ) ).build() );

        final Node child1 =
            createNode( CreateNodeParams.create().parent( node1.path() ).name( "child1" ).setNodeId( NodeId.from( "child1" ) ).build() );

        final Node child2 =
            createNode( CreateNodeParams.create().parent( node1.path() ).name( "child2" ).setNodeId( NodeId.from( "child2" ) ).build() );

        final Node child1_1 = createNode(
            CreateNodeParams.create().parent( child1.path() ).name( "child1_1" ).setNodeId( NodeId.from( "child1_1" ) ).build() );

        final Node child1_1_1 = createNode(
            CreateNodeParams.create().parent( child1_1.path() ).name( "child1_1_1" ).setNodeId( NodeId.from( "child1_1_1" ) ).build() );

        final Node child2_1 = createNode(
            CreateNodeParams.create().parent( child2.path() ).name( "child2_1" ).setNodeId( NodeId.from( "child2_1" ) ).build() );

        final PushNodesResult result =
            pushNodes( WS_OTHER, node1.id(), node2.id(), child1.id(), child1_1.id(), child1_1_1.id(), child2.id(), child2_1.id() );

        assertNotNull( getNodeByPathInOther( new NodePath( node1.path(), child1.name() ) ) );

        final Node movedNode = moveNode( node1.id(), node2.path() );

        pushNodes( WS_OTHER, node1.id() );

        assertNotNull( getNodeByPathInOther( new NodePath( movedNode.path(), child1.name() ) ) );

        assertNull( getNodeByPathInOther( new NodePath( node1.path(), child1.name() ) ) );

        Node child1Node = ctxOther().callWith( () -> getNodeById( child1.id() ) );
        assertNotNull( getNodeByPathInOther( new NodePath( child1Node.path(), child1_1.name() ) ) );
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
            setNodeId( NodeId.from( "child1" ) ).build() );

        final Node child1_1 = createNode(
            CreateNodeParams.create().parent( child1.path() ).name( "child1_1" ).setNodeId( NodeId.from( "child1_1" ) ).build() );

        pushNodes( WS_OTHER, parent.id(), child1.id() );

        renameNode( parent.id(), "parent-renamed" );
        renameNode( child1.id(), "child1-renamed" );
        renameNode( child1_1.id(), "child1_1-renamed" );

        final PushNodesResult result = pushNodes( WS_OTHER, parent.id(), child1.id() );

        assertEquals( 2, result.getSuccessful().size() );
    }

    @Test
    void push_moved()
    {
        final Node a =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "a" ).setNodeId( NodeId.from( "a-id" ) ).build() );

        final Node b =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "b" ).setNodeId( NodeId.from( "b-id" ) ).build() );

        final Node c = createNode( CreateNodeParams.create().parent( b.path() ).name( "c" ).setNodeId( NodeId.from( "c-id" ) ).build() );

        final Node d = createNode( CreateNodeParams.create().parent( c.path() ).name( "d" ).setNodeId( NodeId.from( "d-id" ) ).build() );

        final NodeIds nodeIds1 = NodeIds.from( a.id(), b.id(), c.id(), d.id() );
        pushNodes( WS_OTHER, nodeIds1.stream().toArray( NodeId[]::new ) );

        moveNode( b.id(), a.path() );

        final PushNodesResult result = pushNodes( WS_OTHER, b.id() );

        assertThat( result.getSuccessful() ).extracting( PushNodeResult::getNodeId, PushNodeResult::getNodePath,
                                                         PushNodeResult::getTargetPath )
            .containsExactly( tuple( b.id(), new NodePath( "/a/b" ), new NodePath( "/b" ) ),
                              tuple( c.id(), new NodePath( "/a/b/c" ), new NodePath( "/b/c" ) ),
                              tuple( d.id(), new NodePath( "/a/b/c/d" ), new NodePath( "/b/c/d" ) ) );
    }

    @Test
    public void rename_to_name_already_there_but_renamed_in_same_push()
        throws Exception
    {
        final Node a = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "a" ).setNodeId( NodeId.from( "a" ) ).build() );

        final Node b = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "b" ).setNodeId( NodeId.from( "b" ) ).build() );

        pushNodes( WS_OTHER, a.id() );

        renameNode( a.id(), "a_old" );
        renameNode( b.id(), "a" );

        final PushNodesResult result = pushNodes( WS_OTHER, b.id(), a.id() );

        assertEquals( 0, result.getFailed().size() );
        assertEquals( 2, result.getSuccessful().size() );
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
            setNodeId( NodeId.from( "child2" ) ).build() );

        final Node child2_1 = createNode(
            CreateNodeParams.create().parent( child2.path() ).name( "child2_1" ).setNodeId( NodeId.from( "child2_1" ) ).build() );

        pushNodes( WS_OTHER, node.id(), node2.id(), child1.id(), child1_1.id(), child1_1_1.id(), child2.id(), child2_1.id() );

        renameNode( node.id(), "node1-renamed" );
        renameNode( child1.id(), "child1-renamed" );
        renameNode( child1_1.id(), "child1_1-renamed" );
        renameNode( child1_1_1.id(), "child1_1_1-renamed" );
        renameNode( node2.id(), "node2-renamed" );
        renameNode( child2.id(), "child2-renamed" );
        renameNode( child2_1.id(), "child2_1-renamed" );

        final PushNodesResult result =
            pushNodes( WS_OTHER, child1_1_1.id(), child1_1.id(), node.id(), child2_1.id(), node2.id(), child1.id(), child2.id() );

        assertEquals( 7, result.getSuccessful().size() );
    }

    @Test
    public void push_after_update()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            setNodeId( NodeId.from( "node1" ) ).
            build() );

        updateNode( UpdateNodeParams.create().
            id( node.id() ).
            editor( toUpdate -> toUpdate.data.addString( "newString", "newValue" ) ).
            build() );

        pushNodes( WS_OTHER, node.id() );

        final Node pushedNode = ctxOther().callWith( () -> nodeService.getById( node.id() ) );

        assertEquals( "newValue", pushedNode.data().getString( "newString" ) );
    }

    @Test
    public void push_with_capital_node_id()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).name( "my-node" ).setNodeId( NodeId.from( "MyNodeId" ) ).build() );

        final Node child = createNode(
            CreateNodeParams.create().parent( node.path() ).name( "my-child" ).setNodeId( NodeId.from( "MyChildId" ) ).build() );

        pushNodes( WS_OTHER, node.id() );
        pushNodes( WS_OTHER, child.id() );

        final Node prodNode = ctxOther().callWith( () -> getNodeById( child.id() ) );

        assertNotNull( prodNode );
    }

    @Test
    void push_more_than_1024_nodes_at_once()
    {
        final Node rootNode = createNodeSkipVerification( CreateNodeParams.create().name( "rootNode" ).parent( NodePath.ROOT ).build() );

        createNodes( rootNode, 10, 3, 1 );

        refresh();

        final ResolveSyncWorkResult syncWork = ResolveSyncWorkCommand.create()
            .nodeId( rootNode.id() )
            .target( WS_OTHER )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        final Stopwatch started = Stopwatch.createStarted();

        final PushNodesResult result = PushNodesCommand.create()
            .ids( syncWork.getNodeComparisons().getNodeIds() )
            .target( WS_OTHER )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        started.stop();

        final long elapsed = started.elapsed( TimeUnit.SECONDS );
        final int number = result.getSuccessful().size();

        System.out.println( "Pushed : " + number + " in " + started + ", " + ( elapsed == 0 ? "n/a" : ( number / elapsed ) + "/s" ) );
    }

    private Node getNodeByPathInOther( final NodePath nodePath )
    {
        return ctxOther().callWith( () -> getNodeByPath( nodePath ) );
    }
}
