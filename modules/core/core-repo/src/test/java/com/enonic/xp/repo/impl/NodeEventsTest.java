package com.enonic.xp.repo.impl;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventConstants;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodeResult;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeEventsTest
{
    @Test
    void testCreated()
    {
        final Node created = createNode( "created", new NodePath( "/mynode1/child1" ), "id" );

        Event event = NodeEvents.created( created, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "[{id=id, path=/mynode1/child1/created, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( EventConstants.NODES_FIELD ).get().toString() );
    }

    @Test
    void testPushed()
    {
        final Node pushed1 = createNode( "pushed1", new NodePath( "/mynode1/pushed1" ), "id1" );
        final Node pushed2 = createNode( "pushed2", new NodePath( "/mynode1/pushed2" ), "id2" );
        final Node pushed3 = createNode( "pushed3Renamed", new NodePath( "/mynode1/pushed3" ), "id3" );

        final List<PushNodeResult> pushNodeEntries = List.of( createPushNodeResult( pushed1, null ), createPushNodeResult( pushed2, null ),
                                                              createPushNodeResult( pushed3, new NodePath( "/mynode1/pushed3/pushed3" ) ) );

        final Context context = createContext( "master" );
        final InternalContext internalContext = InternalContext.from( context );
        Event event = NodeEvents.pushed( pushNodeEntries, internalContext );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertTrue( event.hasValue( EventConstants.NODES_FIELD ) );
        assertEquals( NodeEvents.NODE_PUSHED_EVENT, event.getType() );
        assertEquals( "[{id=id1, path=/mynode1/pushed1/pushed1, branch=master, repo=com.enonic.cms.myproject}" +
                          ", {id=id2, path=/mynode1/pushed2/pushed2, branch=master, repo=com.enonic.cms.myproject}" +
                          ", {id=id3, path=/mynode1/pushed3/pushed3Renamed, branch=master, repo=com.enonic.cms.myproject, currentTargetPath=/mynode1/pushed3/pushed3}]",
                      event.getValue( EventConstants.NODES_FIELD ).get().toString() );
    }

    private static PushNodeResult createPushNodeResult( final Node pushed, final NodePath targetPath )
    {
        return PushNodeResult.success( pushed.id(), pushed.getNodeVersionId(), pushed.path(), targetPath );
    }

    @Test
    void testDeleted()
    {
        final Node deleted = createNode( "deleted", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.created( deleted, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "[{id=myId, path=/mynode1/child1/deleted, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( EventConstants.NODES_FIELD ).get().toString() );
    }

    @Test
    void testDuplicated()
    {
        final Node duplicated = createNode( "duplicated", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.duplicated( duplicated, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_DUPLICATED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/duplicated, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( EventConstants.NODES_FIELD ).get().toString() );
    }

    @Test
    void testUpdated()
    {
        final Node updated = createNode( "updated", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.updated( updated, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_UPDATED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/updated, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( EventConstants.NODES_FIELD ).get().toString() );
    }

    @Test
    void testMoved()
    {
        final Node sourceNode = createNode( "before", new NodePath( "/mynode1/child1" ), "myId" );
        final Node targetNode = createNode( "after", new NodePath( "/mynode1" ), "myId" );

        Event event =
            NodeEvents.moved( List.of( MoveNodeResult.MovedNode.create().node( targetNode ).previousPath( sourceNode.path() ).build() ),
                              InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_MOVED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/before, branch=draft, repo=com.enonic.cms.myproject, newPath=/mynode1/after}]",
                      event.getValue( EventConstants.NODES_FIELD ).get().toString() );
    }

    @Test
    void testRenamed()
    {
        final Node sourceNode = createNode( "before", new NodePath( "/mynode1/child1" ), "myId" );
        final Node targetNode = createNode( "after", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.renamed( MoveNodeResult.MovedNode.create().node( targetNode ).previousPath( sourceNode.path() ).build(),
                                          InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_RENAMED_EVENT, event.getType() );
        assertEquals(
            "[{id=myId, path=/mynode1/child1/before, branch=draft, repo=com.enonic.cms.myproject, newPath=/mynode1/child1/after}]",
            event.getValue( EventConstants.NODES_FIELD ).get().toString() );
    }

    @Test
    void testSorted()
    {
        final Node sorted = createNode( "sorted", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.sorted( sorted, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_SORTED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/sorted, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( EventConstants.NODES_FIELD ).get().toString() );
    }

    private Node createNode( final String name, final NodePath root, String id )
    {
        return Node.create().name( NodeName.from( name ) ).parentPath( root ).nodeVersionId( new NodeVersionId() ).id( NodeId.from( id ) ).build();
    }

    private Context createContext( final String branch )
    {
        return ContextBuilder.create().branch( branch ).repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) ).build();
    }

}
