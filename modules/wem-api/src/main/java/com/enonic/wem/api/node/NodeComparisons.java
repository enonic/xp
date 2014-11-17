package com.enonic.wem.api.node;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class NodeComparisons
    implements Iterable<NodeComparison>
{
    private final ImmutableSet<NodeComparison> nodeComparisons;

    private NodeComparisons( Builder builder )
    {
        nodeComparisons = ImmutableSet.copyOf( builder.nodeComparisons );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<NodeComparison> iterator()
    {
        return nodeComparisons.iterator();
    }

    public static final class Builder
    {
        private final Set<NodeComparison> nodeComparisons = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder add( final NodeComparison nodeComparison )
        {
            nodeComparisons.add( nodeComparison );
            return this;
        }

        public NodeComparisons build()
        {
            return new NodeComparisons( this );
        }
    }
}
