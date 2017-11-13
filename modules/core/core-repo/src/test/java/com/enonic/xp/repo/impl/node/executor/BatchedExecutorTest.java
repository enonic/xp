package com.enonic.xp.repo.impl.node.executor;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

import static org.junit.Assert.*;

public class BatchedExecutorTest
{

    @Test
    public void batched_execution()
        throws Exception
    {
        final ExecutorCommand<NodeIds> nodeIdExecutorCommand = new NodeIdExecutorCommand( 1000 );

        final BatchedExecutor<NodeIds> executor = new BatchedExecutor<>( nodeIdExecutorCommand, 100 );

        final NodeIds.Builder builder = NodeIds.create();

        while ( executor.hasMore() )
        {
            builder.addAll( executor.execute() );
        }

        final NodeIds result = builder.build();
        assertEquals( 1000, result.getSize() );
    }

    @Test
    public void batched_execution_rest()
        throws Exception
    {
        final ExecutorCommand<NodeIds> nodeIdExecutorCommand = new NodeIdExecutorCommand( 1001 );

        final BatchedExecutor<NodeIds> executor = new BatchedExecutor<>( nodeIdExecutorCommand, 100 );

        final NodeIds.Builder builder = NodeIds.create();

        while ( executor.hasMore() )
        {
            builder.addAll( executor.execute() );
        }

        final NodeIds result = builder.build();
        assertEquals( 1001, result.getSize() );
    }

    private class NodeIdExecutorCommand
        implements ExecutorCommand<NodeIds>
    {
        final List<NodeId> nodeIds;

        NodeIdExecutorCommand( final int size )
        {
            this.nodeIds = createNodeIds( size );
        }

        private List<NodeId> createNodeIds( int count )
        {
            List<NodeId> values = Lists.newArrayList();

            for ( int i = 0; i < count; i++ )
            {
                values.add( NodeId.from( "node-" + i ) );
            }

            return values;
        }

        @Override
        public long getTotalHits()
        {
            return nodeIds.size();
        }

        @Override
        public ExecutorCommandResult<NodeIds> execute( final int from, final int size )
        {
            return new NodeIdsResult(
                NodeIds.from( this.nodeIds.subList( from, ( from + size < nodeIds.size() ? from + size : nodeIds.size() ) ) ) );
        }
    }

    private class NodeIdsResult
        implements ExecutorCommandResult<NodeIds>
    {
        private final NodeIds nodeIds;

        NodeIdsResult( final NodeIds nodeIds )
        {
            this.nodeIds = nodeIds;
        }

        @Override
        public boolean isEmpty()
        {
            return nodeIds.isEmpty();
        }

        @Override
        public NodeIds get()
        {
            return nodeIds;
        }
    }
}