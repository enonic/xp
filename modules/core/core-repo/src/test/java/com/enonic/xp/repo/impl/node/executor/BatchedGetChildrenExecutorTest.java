package com.enonic.xp.repo.impl.node.executor;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;

import static org.junit.Assert.*;

public class BatchedGetChildrenExecutorTest
    extends AbstractNodeTest
{

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        createDefaultRootNode();
    }

    @Test
    public void batched()
        throws Exception
    {
        createNode( NodePath.ROOT, "node1" );
        createNode( NodePath.ROOT, "node2" );
        createNode( NodePath.ROOT, "node3" );
        createNode( NodePath.ROOT, "node4" );
        createNode( NodePath.ROOT, "node5" );

        final BatchedGetChildrenExecutor executor = BatchedGetChildrenExecutor.create().
            nodeService( this.nodeService ).
            parentId( Node.ROOT_UUID ).
            batchSize( 2 ).
            build();

        final NodeIds.Builder builder = NodeIds.create();

        while ( executor.hasMore() )
        {
            builder.addAll( executor.execute() );
        }

        final NodeIds result = builder.build();
        assertEquals( 5, result.getSize() );
    }

    @Test
    public void recursive()
        throws Exception
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        final Node node1_1 = createNode( node1.path(), "node1_1" );
        final Node node1_1_1 = createNode( node1_1.path(), "node1_1_1" );
        final Node node1_1_1_1 = createNode( node1_1_1.path(), "node1_1_1_1" );
        createNode( node1_1_1_1.path(), "node1_1_1_1_1" );

        final BatchedGetChildrenExecutor executor = BatchedGetChildrenExecutor.create().
            nodeService( this.nodeService ).
            parentId( Node.ROOT_UUID ).
            recursive( true ).
            batchSize( 2 ).
            build();

        final NodeIds.Builder builder = NodeIds.create();

        while ( executor.hasMore() )
        {
            builder.addAll( executor.execute() );
        }

        final NodeIds result = builder.build();
        assertEquals( 5, result.getSize() );
    }

    @Test
    public void child_order()
        throws Exception
    {
        createNode( NodePath.ROOT, "d" );
        createNode( NodePath.ROOT, "e" );
        createNode( NodePath.ROOT, "a" );
        createNode( NodePath.ROOT, "b" );
        createNode( NodePath.ROOT, "c" );

        final BatchedGetChildrenExecutor executor = BatchedGetChildrenExecutor.create().
            nodeService( this.nodeService ).
            parentId( Node.ROOT_UUID ).
            batchSize( 2 ).
            childOrder( ChildOrder.from( "_name ASC" ) ).
            build();

        final NodeIds.Builder builder = NodeIds.create();

        while ( executor.hasMore() )
        {
            builder.addAll( executor.execute() );
        }

        final NodeIds result = builder.build();
        assertEquals( 5, result.getSize() );

        final Iterator<NodeId> iterator = result.iterator();
        assertEquals( iterator.next(), NodeId.from( "a" ));
        assertEquals( iterator.next(), NodeId.from( "b" ));
        assertEquals( iterator.next(), NodeId.from( "c" ));
        assertEquals( iterator.next(), NodeId.from( "d" ));
        assertEquals( iterator.next(), NodeId.from( "e" ));
    }

}