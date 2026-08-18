package com.enonic.xp.node;

import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;

/**
 * Result of {@link NodeService#list(ListNodesParams)}, holding every listed node the caller is permitted to read, ordered by path.
 * Where the listing is consumed in batches, an instance is one batch, and {@link #getCursor()} says whether and where it continues.
 *
 * @since 8.1.0
 */
@NullMarked
public final class ListNodesResult
{
    private final ImmutableList<NodeListEntry> entries;

    @Nullable
    private final String cursor;

    private ListNodesResult( final Builder builder )
    {
        this.entries = builder.entries.build();
        this.cursor = builder.cursor;
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
     * The number of nodes listed. For an unbatched listing this is the total; for a batch it counts that batch alone.
     */
    public int getSize()
    {
        return entries.size();
    }

    /**
     * Whether this result lists no nodes. An empty batch does not end a batched listing: entries the caller is not permitted to read
     * still advance the cursor, so a batch may come back empty while {@link #getCursor()} still names a continuation.
     */
    public boolean isEmpty()
    {
        return entries.isEmpty();
    }

    /**
     * Where the listing continues, or {@code null} where it is exhausted. Pass a non-null cursor unchanged to
     * {@link ListNodesParams.Builder#cursor(String)} of the next call, and treat the listing as finished only when a batch answers with
     * {@code null} — whether a batch holds entries says nothing about whether it is the last.
     * <p>
     * Always {@code null} for an unbatched listing.
     */
    public @Nullable String getCursor()
    {
        return cursor;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<NodeListEntry> entries = ImmutableList.builder();

        @Nullable
        private String cursor;

        private Builder()
        {
        }

        public Builder addEntry( final NodeListEntry entry )
        {
            this.entries.add( entry );
            return this;
        }

        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        public ListNodesResult build()
        {
            return new ListNodesResult( this );
        }
    }
}
