package com.enonic.xp.repo.impl.node.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.InternalContext;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.PushNodeEntries;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.storage.NodeMovedParams;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

public class NodeEventListenerTest
{
    private NodeEventListener nodeEventListener;

    private NodeStorageService nodeStorageService;

    @Before
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
        nodeEventListener.onEvent( Event.create( NodeEvents.NODE_CREATED_EVENT ).
            value( "fisk", "ost" ).
            localOrigin( false ).
            build() );

        Mockito.verify( nodeStorageService, Mockito.never() ).handleNodeCreated( Mockito.any(), Mockito.any(), Mockito.any() );
    }

    @Test
    public void invalid_event_nodes_not_list()
        throws Exception
    {
        nodeEventListener.onEvent( Event.create( NodeEvents.NODE_CREATED_EVENT ).
            value( "nodes", "ost" ).
            localOrigin( false ).
            build() );

        Mockito.verify( nodeStorageService, Mockito.never() ).handleNodeCreated( Mockito.any(), Mockito.any(), Mockito.any() );
    }

    @Test
    public void local_event_ignored()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final Event localEvent = NodeEvents.created( Node.create().
            id( nodeId ).
            parentPath( nodePath.getParentPath() ).
            name( nodePath.getLastElement().toString() ).
            build() );

        nodeEventListener.onEvent( localEvent );

        Mockito.verify( nodeStorageService, Mockito.never() ).handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
                                                                                 Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_create_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final Event localEvent = NodeEvents.created( Node.create().
            id( nodeId ).
            parentPath( nodePath.getParentPath() ).
            name( nodePath.getLastElement().toString() ).
            build() );

        nodeEventListener.onEvent( Event.create( localEvent ).
            localOrigin( false ).
            build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
                                                                                    Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_delete_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final NodeBranchEntry nodeBranchEntry = NodeBranchEntry.create().nodeId( nodeId ).nodePath( nodePath ).
            nodeState( NodeState.DEFAULT ).build();

        final Event localEvent = NodeEvents.deleted( NodeBranchEntries.create().add( nodeBranchEntry ).build() );

        nodeEventListener.onEvent( Event.create( localEvent ).
            localOrigin( false ).
            build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).handleNodeDeleted( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
                                                                                    Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_moved_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final Node sourceNode = Node.create().
            id( nodeId ).
            parentPath( nodePath.getParentPath() ).
            name( nodePath.getLastElement().toString() ).
            build();

        final Node movedNode = Node.create( sourceNode ).
            parentPath( NodePath.create( "newParent" ).build() ).
            build();

        final Event localEvent = NodeEvents.moved( sourceNode, movedNode );

        nodeEventListener.onEvent( Event.create( localEvent ).
            localOrigin( false ).
            build() );

        final NodeMovedParams nodeMovedParams = new NodeMovedParams( sourceNode.path(), movedNode.path(), sourceNode.id() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).handleNodeMoved( Mockito.eq( nodeMovedParams ),
                                                                                  Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_renamed_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final Node sourceNode = Node.create().
            id( nodeId ).
            parentPath( nodePath.getParentPath() ).
            name( nodePath.getLastElement().toString() ).
            build();

        final Node movedNode = Node.create( sourceNode ).
            parentPath( NodePath.create( "newParent" ).build() ).
            build();

        final Event localEvent = NodeEvents.renamed( sourceNode, movedNode );

        nodeEventListener.onEvent( Event.create( localEvent ).
            localOrigin( false ).
            build() );

        final NodeMovedParams nodeMovedParams = new NodeMovedParams( sourceNode.path(), movedNode.path(), sourceNode.id() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).handleNodeMoved( Mockito.eq( nodeMovedParams ),
                                                                                  Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_duplicated_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final Node sourceNode = Node.create().
            id( nodeId ).
            parentPath( nodePath.getParentPath() ).
            name( nodePath.getLastElement().toString() ).
            build();

        final Node movedNode = Node.create( sourceNode ).
            parentPath( NodePath.create( "newParent" ).build() ).
            build();

        final Event localEvent = NodeEvents.duplicated( sourceNode );

        nodeEventListener.onEvent( Event.create( localEvent ).
            localOrigin( false ).
            build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
                                                                                    Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_pushed_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();
        final NodePath previousNodePath = NodePath.create( NodePath.ROOT, "previousName" ).build();

        final NodeBranchEntry nodeBranchEntry = NodeBranchEntry.create().
            nodeId( nodeId ).
            nodePath( nodePath ).
            nodeState( NodeState.DEFAULT ).
            build();
        final PushNodeEntry pushNodeEntry = PushNodeEntry.create().
            nodeBranchEntry( nodeBranchEntry ).
            currentTargetPath( previousNodePath ).
            build();

        final PushNodeEntries pushNodeEntries = PushNodeEntries.create().
            targetRepo( ContentConstants.CONTENT_REPO.getId() ).
            targetBranch( ContentConstants.BRANCH_MASTER ).
            add( pushNodeEntry ).
            build();

        final Event localEvent = NodeEvents.pushed( pushNodeEntries );

        nodeEventListener.onEvent( Event.create( localEvent ).
            localOrigin( false ).
            build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).handleNodePushed( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
                                                                                   Mockito.eq( previousNodePath ),
                                                                                   Mockito.isA( InternalContext.class ) );
    }
}
