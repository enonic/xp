package com.enonic.xp.node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class ResolveSyncWorkResult
    implements Iterable<NodeComparison>
{
    private final NodeComparisons nodeComparisons;

    private ResolveSyncWorkResult( final Builder builder )
    {
        nodeComparisons = builder.comparisonsBuilder.build();
    }

    private ResolveSyncWorkResult( final Set<NodeComparison> nodeComparisons )
    {
        this.nodeComparisons = NodeComparisons.create().
            addAll( nodeComparisons ).
            build();
    }

    public static ResolveSyncWorkResult empty()
    {
        return new ResolveSyncWorkResult( new HashSet<>() );
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

    @Override
    public Iterator<NodeComparison> iterator()
    {
        return this.nodeComparisons.iterator();
    }

    public static final class Builder
    {
        private final NodeComparisons.Builder comparisonsBuilder = NodeComparisons.create();

        private Builder()
        {
        }

        public Builder add( final NodeComparison val )
        {
            this.comparisonsBuilder.add( val );
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
