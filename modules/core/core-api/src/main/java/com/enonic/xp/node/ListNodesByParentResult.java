package com.enonic.xp.node;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Result of {@link NodeService#list(ListNodesByParentParams)}: every listed node as a lightweight entry, ordered by path.
 *
 * @since 8.1.0
 */
public final class ListNodesByParentResult
{
    private final ImmutableList<NodeListEntry> entries;

    private ListNodesByParentResult( final Builder builder )
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

        public Builder addEntry( final NodeListEntry entry )
        {
            this.entries.add( entry );
            return this;
        }

        public ListNodesByParentResult build()
        {
            return new ListNodesByParentResult( this );
        }
    }
}
