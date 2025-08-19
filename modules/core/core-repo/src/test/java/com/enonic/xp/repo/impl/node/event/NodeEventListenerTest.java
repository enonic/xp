package com.enonic.xp.repo.impl.node.event;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.storage.NodeMovedParams;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeEventListenerTest
{
    private NodeEventListener nodeEventListener;

    private NodeStorageService nodeStorageService;

    @BeforeEach
    public void setUp()
        throws Exception
    {

        nodeEventListener = new NodeEventListener();
        nodeStorageService = Mockito.mock( NodeStorageService.class );
        nodeEventListener.setNodeStorageService( nodeStorageService );

    }

    @Test
    public void invalid_event_no_list()
        throws Exception
    {
        nodeEventListener.onEvent( Event.create( NodeEvents.NODE_CREATED_EVENT ).value( "fisk", "ost" ).localOrigin( false ).build() );

        Mockito.verify( nodeStorageService, Mockito.never() ).handleNodeCreated( Mockito.any(), Mockito.any(), Mockito.any() );
    }

    @Test
    public void invalid_event_nodes_not_list()
        throws Exception
    {
        nodeEventListener.onEvent( Event.create( NodeEvents.NODE_CREATED_EVENT ).value( "nodes", "ost" ).localOrigin( false ).build() );

        Mockito.verify( nodeStorageService, Mockito.never() ).handleNodeCreated( Mockito.any(), Mockito.any(), Mockito.any() );
    }

    @Test
    public void local_event_ignored()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final Event localEvent =
            NodeEvents.created( Node.create().id( nodeId ).parentPath( nodePath.getParentPath() ).name( nodePath.getName() ).build(),
                                createInternalContext() );

        nodeEventListener.onEvent( localEvent );

        Mockito.verify( nodeStorageService, Mockito.never() )
            .handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ), Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_create_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final Event localEvent =
            NodeEvents.created( Node.create().id( nodeId ).parentPath( nodePath.getParentPath() ).name( nodePath.getName() ).build(),
                                createInternalContext() );

        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) )
            .handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ), Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_delete_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final NodeBranchEntry nodeBranchEntry = NodeBranchEntry.create().nodeId( nodeId ).nodePath( nodePath ).build();

        final Event localEvent = NodeEvents.deleted( NodeBranchEntries.create().add( nodeBranchEntry ).build(), createInternalContext() );

        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) )
            .handleNodeDeleted( Mockito.eq( nodeId ), Mockito.eq( nodePath ), Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_moved_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final Node sourceNode = Node.create().id( nodeId ).parentPath( nodePath.getParentPath() ).name( nodePath.getName() ).build();

        final Node movedNode = Node.create( sourceNode ).parentPath( new NodePath( "/newParent" ) ).build();

        final Event localEvent = NodeEvents.moved( MoveNodeResult.create()
                                                       .addMovedNode( MoveNodeResult.MovedNode.create()
                                                                          .node( movedNode )
                                                                          .previousPath( sourceNode.path() )
                                                                          .build() )
                                                       .build(), createInternalContext() );

        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        ArgumentCaptor<NodeMovedParams> captor = ArgumentCaptor.forClass( NodeMovedParams.class );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).handleNodeMoved( captor.capture(), Mockito.isA( InternalContext.class ) );
        assertEquals( sourceNode.path(), captor.getValue().getExistingPath() );
        assertEquals( movedNode.path(), captor.getValue().getNewPath() );
        assertEquals( sourceNode.id(), captor.getValue().getNodeId() );

    }

    @Test
    public void node_renamed_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final Node sourceNode = Node.create().id( nodeId ).parentPath( nodePath.getParentPath() ).name( nodePath.getName() ).build();

        final Node movedNode = Node.create( sourceNode ).parentPath( new NodePath( "/newParent" ) ).build();

        final Event localEvent = NodeEvents.renamed( sourceNode.path(), movedNode, createInternalContext() );
        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        ArgumentCaptor<NodeMovedParams> captor = ArgumentCaptor.forClass( NodeMovedParams.class );
        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).handleNodeMoved( captor.capture(), Mockito.isA( InternalContext.class ) );
        assertEquals( sourceNode.path(), captor.getValue().getExistingPath() );
        assertEquals( movedNode.path(), captor.getValue().getNewPath() );
        assertEquals( sourceNode.id(), captor.getValue().getNodeId() );
    }

    @Test
    public void node_duplicated_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final Node sourceNode = Node.create().id( nodeId ).parentPath( nodePath.getParentPath() ).name( nodePath.getName() ).build();

        final Event localEvent = NodeEvents.duplicated( sourceNode, createInternalContext() );
        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) )
            .handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ), Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_pushed_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );
        final NodePath previousNodePath = new NodePath( "/previousName" );

        final NodeBranchEntry nodeBranchEntry = NodeBranchEntry.create().nodeId( nodeId ).nodePath( nodePath ).build();
        final PushNodeEntry pushNodeEntry =
            PushNodeEntry.create().nodeBranchEntry( nodeBranchEntry ).currentTargetPath( previousNodePath ).build();

        final InternalContext internalContext = createInternalContext();
        final Event localEvent = NodeEvents.pushed( List.of( pushNodeEntry ), internalContext );

        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) )
            .handleNodePushed( Mockito.eq( nodeId ), Mockito.eq( nodePath ), Mockito.eq( previousNodePath ),
                               Mockito.isA( InternalContext.class ) );
    }

    private InternalContext createInternalContext()
    {
        return InternalContext.from( ContextBuilder.from( ContextAccessor.current() )
                                         .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
                                         .branch( ContentConstants.BRANCH_DRAFT )
                                         .build() );
    }
}
