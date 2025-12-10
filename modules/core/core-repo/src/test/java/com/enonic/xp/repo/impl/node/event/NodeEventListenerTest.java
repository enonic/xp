package com.enonic.xp.repo.impl.node.event;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.PushNodeResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeEventListenerTest
{
    private NodeEventListener nodeEventListener;

    private NodeStorageService nodeStorageService;

    @BeforeEach
    void setUp()
    {
        nodeEventListener = new NodeEventListener();
        nodeStorageService = Mockito.mock( NodeStorageService.class );
        nodeEventListener.setNodeStorageService( nodeStorageService );
    }

    @Test
    void node_delete_event()
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final NodeBranchEntry nodeBranchEntry = NodeBranchEntry.create()
            .nodeId( nodeId )
            .nodeVersionId( new NodeVersionId() )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( BlobKey.from( "nodeBlobKey" ) )
                                 .indexConfigBlobKey( BlobKey.from( "indexConfigBlobKey" ) )
                                 .accessControlBlobKey( BlobKey.from( "accessControlBlobKey" ) )
                                 .build() )
            .timestamp( Instant.EPOCH )
            .nodePath( nodePath )
            .build();
        final Event localEvent = NodeEvents.deleted( NodeBranchEntries.create().add( nodeBranchEntry ).build(), createInternalContext() );

        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) )
            .invalidatePath( Mockito.eq( nodePath ), Mockito.isA( InternalContext.class ) );
    }

    @Test
    void node_moved_event()
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final Node sourceNode = Node.create().id( nodeId ).parentPath( nodePath.getParentPath() ).name( nodePath.getName() ).build();

        final Node movedNode = Node.create( sourceNode ).parentPath( new NodePath( "/newParent" ) ).build();

        final Event localEvent = NodeEvents.moved( List.of( MoveNodeResult.MovedNode.create()
                                                                          .node( movedNode )
                                                                          .previousPath( sourceNode.path() )
                                                                          .build() ), createInternalContext() );

        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        ArgumentCaptor<NodePath> captor = ArgumentCaptor.forClass( NodePath.class );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).invalidatePath( captor.capture(), Mockito.isA( InternalContext.class ) );
        assertEquals( sourceNode.path(), captor.getValue() );
    }

    @Test
    void node_renamed_event()
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );

        final Node sourceNode = Node.create().id( nodeId ).parentPath( nodePath.getParentPath() ).name( nodePath.getName() ).build();

        final Node movedNode = Node.create( sourceNode ).parentPath( new NodePath( "/newParent" ) ).build();

        final Event localEvent =  NodeEvents.moved( List.of(MoveNodeResult.MovedNode.create().node( movedNode ).previousPath( sourceNode.path() ).build()),
                                          createInternalContext() );
        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        ArgumentCaptor<NodePath> captor = ArgumentCaptor.forClass( NodePath.class );
        Mockito.verify( nodeStorageService, Mockito.times( 1 ) ).invalidatePath( captor.capture(), Mockito.isA( InternalContext.class ) );
        assertEquals( sourceNode.path(), captor.getValue() );
    }

    @Test
    void node_pushed_event()
    {
        final NodeId nodeId = NodeId.from( "node1" );
        final NodePath nodePath = new NodePath( "/nodeName" );
        final NodePath previousNodePath = new NodePath( "/previousName" );

        final InternalContext internalContext = createInternalContext();
        final Event localEvent =
            NodeEvents.pushed( List.of( PushNodeResult.success( nodeId, new NodeVersionId(), nodePath, previousNodePath ) ),
                               internalContext );

        nodeEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );

        Mockito.verify( nodeStorageService, Mockito.times( 1 ) )
            .invalidatePath( Mockito.eq( previousNodePath ), Mockito.isA( InternalContext.class ) );
    }

    private InternalContext createInternalContext()
    {
        return InternalContext.from( ContextBuilder.from( ContextAccessor.current() )
                                         .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
                                         .branch( ContentConstants.BRANCH_DRAFT )
                                         .build() );
    }
}
