package com.enonic.xp.node;

import java.util.List;

import org.jspecify.annotations.NullMarked;

import com.google.common.collect.ImmutableList;

/**
 * Result of {@link NodeService#list(ListNodesParams)}, holding every listed node the caller is permitted to read, ordered by path.
 *
 * @since 8.1.0
 */
@NullMarked
public final class ListNodesResult
{
    private final ImmutableList<NodeListEntry> entries;

    private ListNodesResult( final Builder builder )
    {
        this.entries = builder.entries.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Every listed node, ordered by path.
     */
    public List<NodeListEntry> getEntries()
    {
        return entries;
    }

    /**
     * The ids of the listed nodes, in the order the entries hold, for passing on to a method that reads nodes by id.
     */
    public NodeIds getNodeIds()
    {
        return entries.stream().map( NodeListEntry::nodeId ).collect( NodeIds.collector() );
    }

    /**
     * The number of nodes listed.
     */
    public int getSize()
    {
        return entries.size();
    }

    /**
     * Whether the listing holds no nodes.
     */
    public boolean isEmpty()
    {
        return entries.isEmpty();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<NodeListEntry> entries = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder addEntry( final NodeListEntry entry )
        {
            this.entries.add( entry );
            return this;
        }

        public ListNodesResult build()
        {
            return new ListNodesResult( this );
        }
    }
}
