package com.enonic.xp.repo.impl;

import org.junit.Test;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.Nodes;

import static org.junit.Assert.*;

public class NodeEventsTest
{

    @Test
    public void testCreated()
    {
        final Node created = createNode( "created", NodePath.create( "/mynode1/child1" ).build(), "id" );

        Event2 event = NodeEvents.created( created );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "[{id=id, path=/mynode1/child1/created}]", event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testPushed()
    {
        final Node pushed1 = createNode( "pushed1", NodePath.create( "/mynode1/pushed1" ).build(), "id1" );
        final Node pushed2 = createNode( "pushed2", NodePath.create( "/mynode1/pushed2" ).build(), "id2" );
        final Node pushed3 = createNode( "pushed3", NodePath.create( "/mynode1/pushed3" ).build(), "id3" );
        final Nodes nodes = Nodes.from( pushed1, pushed2, pushed3 );

        Event2 event = NodeEvents.pushed( nodes );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertTrue( event.hasValue( "nodes" ) );
        assertEquals( NodeEvents.NODE_PUSHED_EVENT, event.getType() );
        assertEquals( "[{id=id1, path=/mynode1/pushed1/pushed1}" +
                          ", {id=id2, path=/mynode1/pushed2/pushed2}" +
                          ", {id=id3, path=/mynode1/pushed3/pushed3}]", event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testDeleted()
    {
        final Node deleted = createNode( "deleted", NodePath.create( "/mynode1/child1" ).build(), "myId" );

        Event2 event = NodeEvents.created( deleted );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "[{id=myId, path=/mynode1/child1/deleted}]", event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testDuplicated()
    {
        final Node duplicated = createNode( "duplicated", NodePath.create( "/mynode1/child1" ).build(), "myId" );

        Event2 event = NodeEvents.duplicated( duplicated );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_DUPLICATED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/duplicated}]", event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testUpdated()
    {
        final Node updated = createNode( "updated", NodePath.create( "/mynode1/child1" ).build(), "myId" );

        Event2 event = NodeEvents.updated( updated );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_UPDATED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/updated}]", event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testRenamed()
    {
        final Node renamed = createNode( "renamed", NodePath.create( "/mynode1/child1" ).build(), "myId" );

        Event2 event = NodeEvents.renamed( renamed );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_RENAMED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/renamed}]", event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testSorted()
    {
        final Node sorted = createNode( "sorted", NodePath.create( "/mynode1/child1" ).build(), "myId" );

        Event2 event = NodeEvents.sorted( sorted );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( NodeEvents.NODE_SORTED_EVENT, event.getType() );
        assertEquals( "[{id=myId, path=/mynode1/child1/sorted}]", event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testStateUpdated()
    {
        final Node pushed1 = createNode( "state_updated1", NodePath.create( "/mynode1/state_updated1" ).build(), "id1" );
        final Node pushed2 = createNode( "state_updated2", NodePath.create( "/mynode1/state_updated2" ).build(), "id2" );
        final Node pushed3 = createNode( "state_updated3", NodePath.create( "/mynode1/state_updated3" ).build(), "id3" );
        final Nodes nodes = Nodes.from( pushed1, pushed2, pushed3 );

        Event2 event = NodeEvents.stateUpdated( nodes );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertTrue( event.hasValue( "nodes" ) );
        assertTrue( event.hasValue( "state" ) );
        assertEquals( NodeEvents.NODE_STATE_UPDATED_EVENT, event.getType() );
        assertEquals( NodeState.DEFAULT.toString(), event.getValue( "state" ).get() );
        assertEquals( "[{id=id1, path=/mynode1/state_updated1/state_updated1}" +
                          ", {id=id2, path=/mynode1/state_updated2/state_updated2}" +
                          ", {id=id3, path=/mynode1/state_updated3/state_updated3}]", event.getValue( "nodes" ).get().toString() );
    }

    @Test
    public void testNullArguments()
    {
        Event2 eventCreated = NodeEvents.created( null );
        Event2 eventDeleted = NodeEvents.deleted( null );

        assertNull( eventCreated );
        assertNull( eventDeleted );
    }

    private Node createNode( final String name, final NodePath root )
    {
        return Node.create().
            name( NodeName.from( name ) ).
            parentPath( root ).
            build();
    }

    private Node createNode( final String name, final NodePath root, String id )
    {
        return Node.create().
            name( NodeName.from( name ) ).
            parentPath( root ).
            id( NodeId.from( id ) ).
            build();
    }

}
