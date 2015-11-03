package com.enonic.xp.repo.impl.node.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.storage.NodeMovedParams;
import com.enonic.xp.repo.impl.storage.StorageService;

public class NodeEventListenerTest
{
    private NodeEventListener nodeEventListener;

    private StorageService storageService;

    @Before
    public void setUp()
        throws Exception
    {

        nodeEventListener = new NodeEventListener();
        storageService = Mockito.mock( StorageService.class );
        nodeEventListener.setStorageService( storageService );

    }

    @Test
    public void invalid_event_no_list()
        throws Exception
    {
        nodeEventListener.onEvent( Event2.create( NodeEvents.NODE_CREATED_EVENT ).
            value( "fisk", "ost" ).
            localOrigin( false ).
            build() );

        Mockito.verify( storageService, Mockito.never() ).handleNodeCreated( Mockito.any(), Mockito.any(), Mockito.any() );
    }

    @Test
    public void invalid_event_nodes_not_list()
        throws Exception
    {
        nodeEventListener.onEvent( Event2.create( NodeEvents.NODE_CREATED_EVENT ).
            value( "nodes", "ost" ).
            localOrigin( false ).
            build() );

        Mockito.verify( storageService, Mockito.never() ).handleNodeCreated( Mockito.any(), Mockito.any(), Mockito.any() );
    }

    @Test
    public void local_event_ignored()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final Event2 localEvent = NodeEvents.created( Node.create().
            id( nodeId ).
            parentPath( nodePath.getParentPath() ).
            name( nodePath.getLastElement().toString() ).
            build() );

        nodeEventListener.onEvent( localEvent );

        Mockito.verify( storageService, Mockito.never() ).handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
                                                                             Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_create_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final Event2 localEvent = NodeEvents.created( Node.create().
            id( nodeId ).
            parentPath( nodePath.getParentPath() ).
            name( nodePath.getLastElement().toString() ).
            build() );

        nodeEventListener.onEvent( Event2.create( localEvent ).
            localOrigin( false ).
            build() );

        Mockito.verify( storageService, Mockito.times( 1 ) ).handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
                                                                                Mockito.isA( InternalContext.class ) );
    }

    @Test
    public void node_delete_event()
        throws Exception
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = NodePath.create( NodePath.ROOT, "nodeName" ).build();

        final Event2 localEvent = NodeEvents.deleted( Node.create().
            id( nodeId ).
            parentPath( nodePath.getParentPath() ).
            name( nodePath.getLastElement().toString() ).
            build() );

        nodeEventListener.onEvent( Event2.create( localEvent ).
            localOrigin( false ).
            build() );

        Mockito.verify( storageService, Mockito.times( 1 ) ).handleNodeDeleted( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
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

        final Event2 localEvent = NodeEvents.moved( sourceNode, movedNode );

        nodeEventListener.onEvent( Event2.create( localEvent ).
            localOrigin( false ).
            build() );

        final NodeMovedParams nodeMovedParams = new NodeMovedParams( sourceNode.path(), movedNode.path(), sourceNode.id() );

        Mockito.verify( storageService, Mockito.times( 1 ) ).handleNodeMoved( Mockito.eq( nodeMovedParams ),
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

        final Event2 localEvent = NodeEvents.renamed( sourceNode, movedNode );

        nodeEventListener.onEvent( Event2.create( localEvent ).
            localOrigin( false ).
            build() );

        final NodeMovedParams nodeMovedParams = new NodeMovedParams( sourceNode.path(), movedNode.path(), sourceNode.id() );

        Mockito.verify( storageService, Mockito.times( 1 ) ).handleNodeMoved( Mockito.eq( nodeMovedParams ),
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

        final Event2 localEvent = NodeEvents.duplicated( sourceNode );

        nodeEventListener.onEvent( Event2.create( localEvent ).
            localOrigin( false ).
            build() );

        Mockito.verify( storageService, Mockito.times( 1 ) ).handleNodeCreated( Mockito.eq( nodeId ), Mockito.eq( nodePath ),
                                                                                Mockito.isA( InternalContext.class ) );
    }


}