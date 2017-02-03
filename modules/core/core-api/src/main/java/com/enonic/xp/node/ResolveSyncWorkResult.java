package com.enonic.xp.node;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

public class ResolveSyncWorkResult
    implements Iterable<NodeComparison>
{
    private NodeComparisons nodeComparisons;

    private NodeIds requiredNodes;

    private ResolveSyncWorkResult( final Builder builder )
    {
        nodeComparisons = builder.comparisonsBuilder.build();
        requiredNodes = builder.requiredNodes.build();
    }

    private ResolveSyncWorkResult( final Set<NodeComparison> nodeComparisons )
    {
        this.nodeComparisons = NodeComparisons.create().
            addAll( nodeComparisons ).
            build();
    }

    public static ResolveSyncWorkResult empty()
    {
        return new ResolveSyncWorkResult( Sets.newHashSet() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public int getSize()
    {
        return this.getNodeComparisons().getSize();
    }

    public NodeComparisons getNodeComparisons()
    {
        return nodeComparisons;
    }

    public NodeIds getRequiredNodes()
    {
        return requiredNodes;
    }

    @Override
    public Iterator<NodeComparison> iterator()
    {
        return this.nodeComparisons.iterator();
    }

    public static final class Builder
    {
        private NodeComparisons.Builder comparisonsBuilder = NodeComparisons.create();

        private NodeIds.Builder requiredNodes = NodeIds.create();

        private Builder()
        {
        }

        public Builder add( final NodeComparison val )
        {
            this.comparisonsBuilder.add( val );
            return this;
        }

        public Builder addRequiredNodes( final NodeIds ids )
        {
            this.requiredNodes.addAll( ids );
            return this;
        }


        public Builder addAll( final Collection<NodeComparison> val )
        {
            this.comparisonsBuilder.addAll( val );
            return this;
        }

        public ResolveSyncWorkResult build()
        {
            return new ResolveSyncWorkResult( this );
        }
    }
}
