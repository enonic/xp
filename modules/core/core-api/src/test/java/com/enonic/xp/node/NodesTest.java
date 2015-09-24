package com.enonic.xp.node;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import static org.junit.Assert.*;

public class NodesTest
{
    @Test
    public void preserves_order_set()
        throws Exception
    {
        final Set<Node> nodeSet = Sets.newLinkedHashSet();

        final Node node1 = Node.create( NodeId.from( "z" ) ).build();
        final Node node2 = Node.create( NodeId.from( "y" ) ).build();
        final Node node3 = Node.create( NodeId.from( "x" ) ).build();

        nodeSet.add( node1 );
        nodeSet.add( node2 );
        nodeSet.add( node3 );

        final Nodes nodes = Nodes.from( nodeSet );

        final Iterator<Node> nodesIterator = nodes.iterator();

        assertEquals( node1, nodesIterator.next() );
        assertEquals( node2, nodesIterator.next() );
        assertEquals( node3, nodesIterator.next() );
    }

    @Test
    public void preserves_order_arguments()
        throws Exception
    {
        final Node node1 = Node.create( NodeId.from( "z" ) ).build();
        final Node node2 = Node.create( NodeId.from( "y" ) ).build();
        final Node node3 = Node.create( NodeId.from( "x" ) ).build();

        final Nodes nodes = Nodes.from( node1, node2, node3 );

        final Iterator<Node> nodesIterator = nodes.iterator();

        assertEquals( node1, nodesIterator.next() );
        assertEquals( node2, nodesIterator.next() );
        assertEquals( node3, nodesIterator.next() );
    }

    @Test
    public void preserves_order_builder()
        throws Exception
    {
        final Node node1 = Node.create( NodeId.from( "z" ) ).build();
        final Node node2 = Node.create( NodeId.from( "y" ) ).build();
        final Node node3 = Node.create( NodeId.from( "x" ) ).build();

        final Nodes.Builder builder = Nodes.create();

        builder.add( node1 );
        builder.add( node2 );
        builder.add( node3 );

        final Iterator<Node> nodesIterator = builder.build().iterator();

        assertEquals( node1, nodesIterator.next() );
        assertEquals( node2, nodesIterator.next() );
        assertEquals( node3, nodesIterator.next() );
    }
}