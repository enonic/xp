package com.enonic.xp.repo.impl.branch.storage;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

public class BatchedBranchEntryExecutor
{
    private final int batchSize;

    private final NodeIds nodeIds;

    private final BranchEntriesExecutorMethod method;

    private static final int DEFAULT_BATCH_SIZE = 1000;

    private BatchedBranchEntryExecutor( final Builder builder )
    {
        batchSize = builder.batchSize;
        nodeIds = builder.nodeIds;
        method = builder.method;
    }

    public NodeBranchEntries execute()
    {
        final NodeBranchEntries.Builder builder = NodeBranchEntries.create();

        final List<List<NodeId>> batchList = Lists.partition( nodeIds.stream().
            collect( Collectors.toList() ), batchSize );

        batchList.forEach( batch -> method.execute( batch, builder ) );

        return builder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeIds nodeIds;

        private BranchEntriesExecutorMethod method;

        private int batchSize = DEFAULT_BATCH_SIZE;

        private Builder()
        {
        }

        public Builder batchSize( final int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        public Builder nodeIds( final NodeIds val )
        {
            nodeIds = val;
            return this;
        }

        public Builder method( final BranchEntriesExecutorMethod val )
        {
            method = val;
            return this;
        }

        public BatchedBranchEntryExecutor build()
        {
            return new BatchedBranchEntryExecutor( this );
        }
    }
}
