package com.enonic.xp.repo.impl;

import org.junit.Test;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class NodeEventsTest
{

    @Test
    public void testMoved()
    {
        final Node from = createNode( "from", NodePath.create( "/mynode1/child1" ).build() );
        final Node to = createNode( "to", NodePath.create( "/mynode2/child2" ).build() );

        Event2 event = NodeEvents.moved( from, to );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "/mynode1/child1/from", from.path().toString() );
        assertEquals( "/mynode2/child2/to", to.path().toString() );
        assertEquals( "/mynode1/child1/from", event.getValue( "path" ).get() );
        assertEquals( "/mynode2/child2/to", event.getValue( "toPath" ).get() );
    }

    @Test
    public void testCreated()
    {
        final Node created = createNode( "created", NodePath.create( "/mynode1/child1" ).build() );

        Event2 event = NodeEvents.created( created );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "/mynode1/child1/created", created.path().toString() );
        assertEquals( "/mynode1/child1/created", event.getValue( "path" ).get() );
    }

    @Test
    public void testDeleted()
    {
        final Node deleted = createNode( "deleted", NodePath.create( "/mynode1/child1" ).build() );

        Event2 event = NodeEvents.created( deleted );

        assertNotNull( event );
        assertTrue( event.isDistributed() );
        assertEquals( "/mynode1/child1/deleted", deleted.path().toString() );
        assertEquals( "/mynode1/child1/deleted", event.getValue( "path" ).get() );
    }

    @Test
    public void testNullArguments()
    {
        Event2 eventCreated = NodeEvents.created( null );
        Event2 eventDeleted = NodeEvents.deleted( null );
        Event2 eventMoved = NodeEvents.moved( null, null );

        assertNull( eventCreated );
        assertNull( eventDeleted );
        assertNull( eventMoved );
    }

    private Node createNode( final String name, final NodePath root )
    {
        return Node.create().
            name( NodeName.from( name ) ).
            parentPath( root ).
            build();
    }
}
