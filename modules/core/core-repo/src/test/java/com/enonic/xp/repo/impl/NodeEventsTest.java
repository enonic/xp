package com.enonic.xp.repo.impl;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodeEventsTest
{

    @Test
    public void testCreated()
    {
        final Node created = createNode( "created", new NodePath( "/mynode1/child1" ), "id" );

        Event event = NodeEvents.created( created, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "[{id=id, path=/mynode1/child1/created, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testPushed()
    {
        final Node pushed1 = createNode( "pushed1", new NodePath( "/mynode1/pushed1" ), "id1" );
        final Node pushed2 = createNode( "pushed2", new NodePath( "/mynode1/pushed2" ), "id2" );
        final Node pushed3 = createNode( "pushed3Renamed", new NodePath( "/mynode1/pushed3" ), "id3" );

        final NodeBranchEntry nodeBranchEntry = createNodeVersionId( pushed1 );
        final NodeBranchEntry nodeBranchEntry2 = createNodeVersionId( pushed2 );
        final NodeBranchEntry nodeBranchEntry3 = createNodeVersionId( pushed3 );

        final List<PushNodeEntry> pushNodeEntries = List.of( PushNodeEntry.create().nodeBranchEntry( nodeBranchEntry ).build(),
                                                             PushNodeEntry.create().nodeBranchEntry( nodeBranchEntry2 ).build(),
                                                             PushNodeEntry.create()
                                                                 .nodeBranchEntry( nodeBranchEntry3 )
                                                                 .currentTargetPath( new NodePath( "/mynode1/pushed3/pushed3" ) )
                                                                 .build() );

        final Context context = createContext( "master" );
        Event event = NodeEvents.pushed( pushNodeEntries, context.getBranch(), context.getRepositoryId() );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertTrue( event.hasValue( "nodes" ) );
        assertEquals( NodeEvents.NODE_PUSHED_EVENT, event.getType() );
        assertEquals( "[{id=id1, path=/mynode1/pushed1/pushed1, branch=master, repo=com.enonic.cms.myproject}" +
                          ", {id=id2, path=/mynode1/pushed2/pushed2, branch=master, repo=com.enonic.cms.myproject}" +
                          ", {id=id3, path=/mynode1/pushed3/pushed3Renamed, branch=master, repo=com.enonic.cms.myproject, currentTargetPath=/mynode1/pushed3/pushed3}]",
                      event.getValue( "nodes" ).get().toString() );
    }

    private static NodeBranchEntry createNodeVersionId( final Node pushed1 )
    {
        return NodeBranchEntry.create()
            .nodeId( pushed1.id() )
            .nodePath( pushed1.path() )
            .nodeVersionKey( NodeVersionKey.from( "nodeBlobKey", "indexConfigBlobKey", "accessControlBlobKey" ) )
            .timestamp( Instant.EPOCH )
            .nodeVersionId( pushed1.getNodeVersionId() )
            .build();
    }

    @Test
    public void testDeleted()
    {
        final Node deleted = createNode( "deleted", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.created( deleted, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "[{id=myId, path=/mynode1/child1/deleted, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testDuplicated()
    {
        final Node duplicated = createNode( "duplicated", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.duplicated( duplicated, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_DUPLICATED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/duplicated, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testUpdated()
    {
        final Node updated = createNode( "updated", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.updated( updated, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_UPDATED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/updated, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testMoved()
    {
        final Node sourceNode = createNode( "before", new NodePath( "/mynode1/child1" ), "myId" );
        final Node targetNode = createNode( "after", new NodePath( "/mynode1" ), "myId" );

        Event event = NodeEvents.moved( MoveNodeResult.create()
                                            .addMovedNode( MoveNodeResult.MovedNode.create()
                                                               .node( targetNode )
                                                               .previousPath( sourceNode.path() )
                                                               .build() )
                                            .build(), InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_MOVED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/before, branch=draft, repo=com.enonic.cms.myproject, newPath=/mynode1/after}]",
                      event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testRenamed()
    {
        final Node sourceNode = createNode( "before", new NodePath( "/mynode1/child1" ), "myId" );
        final Node targetNode = createNode( "after", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.renamed( sourceNode.path(), targetNode, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_RENAMED_EVENT, event.getType() );
        assertEquals(
            "[{id=myId, path=/mynode1/child1/before, branch=draft, repo=com.enonic.cms.myproject, newPath=/mynode1/child1/after}]",
            event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testSorted()
    {
        final Node sorted = createNode( "sorted", new NodePath( "/mynode1/child1" ), "myId" );

        Event event = NodeEvents.sorted( sorted, InternalContext.from( createContext( "draft" ) ) );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_SORTED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/sorted, branch=draft, repo=com.enonic.cms.myproject}]",
                      event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testNullArguments()
    {
        Event eventCreated = NodeEvents.created( null, InternalContext.from( createContext( "draft" ) ) );

        assertNull( eventCreated );
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
