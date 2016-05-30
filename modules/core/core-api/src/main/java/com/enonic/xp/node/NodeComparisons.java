package com.enonic.xp.node;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.content.CompareStatus;

@Beta
public class NodeComparisons
    implements Iterable<NodeComparison>
{
    private final ImmutableSet<NodeComparison> nodeComparisons;

    private final Map<NodeId, NodeComparison> comparisonMap;

    private NodeComparisons( Builder builder )
    {
        nodeComparisons = ImmutableSet.copyOf( builder.nodeComparisons );
        this.comparisonMap = builder.nodeIdNodeComparisonMap;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeComparison get( final NodeId nodeId )
    {
        return this.comparisonMap.get( nodeId );
    }

    @Override
    public Iterator<NodeComparison> iterator()
    {
        return nodeComparisons.iterator();
    }

    public int getSize()
    {
        return this.nodeComparisons.size();
    }

    public NodeIds getNodeIds()
    {
        return NodeIds.from( nodeComparisons.stream().
            map( NodeComparison::getNodeId ).
            collect( Collectors.toSet() ) );
    }

    public Set<NodeComparison> getWithStatus( final CompareStatus status )
    {
        Set<NodeComparison> result = Sets.newHashSet();

        result.addAll( this.nodeComparisons.stream().filter( nodeComparison -> nodeComparison.getCompareStatus() == status ).collect(
            Collectors.toList() ) );

        return result;
    }

    public ImmutableSet<NodeComparison> getSet()
    {
        return nodeComparisons;
    }

    public static final class Builder
    {
        private final Set<NodeComparison> nodeComparisons = Sets.newHashSet();

        private final Map<NodeId, NodeComparison> nodeIdNodeComparisonMap = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final NodeComparison nodeComparison )
        {
            this.nodeComparisons.add( nodeComparison );
            this.nodeIdNodeComparisonMap.put( nodeComparison.getNodeId(), nodeComparison );
            return this;
        }

        public Builder addAll( final Collection<NodeComparison> nodeComparisons )
        {
            this.nodeComparisons.addAll( nodeComparisons );
            nodeComparisons.stream().
                forEach( comparison -> this.nodeIdNodeComparisonMap.put( comparison.getNodeId(), comparison ) );

            return this;
        }

        public NodeComparisons build()
        {
            return new NodeComparisons( this );
        }
    }
}
