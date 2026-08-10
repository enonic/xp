package com.enonic.xp.node;

import java.util.List;

import org.jspecify.annotations.NullMarked;

import com.google.common.collect.ImmutableList;

/**
 * Result of {@link NodeService#list(ListNodesParams)}, holding every node the caller is permitted to read, ordered by path.
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

    public List<NodeListEntry> getEntries()
    {
        return entries;
    }

    public NodeIds getNodeIds()
    {
        return entries.stream().map( NodeListEntry::nodeId ).collect( NodeIds.collector() );
    }

    public int getSize()
    {
        return entries.size();
    }

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

        /**
         * Appends an entry. Entries are expected to be added in path order, which is the order the result is documented to hold.
         */
        public Builder addEntry( final NodeListEntry entry )
        {
            this.entries.add( entry );
            return this;
        }

        /**
         * @return a result holding the entries added so far, in the order they were added.
         */
        public ListNodesResult build()
        {
            return new ListNodesResult( this );
        }
    }
}
