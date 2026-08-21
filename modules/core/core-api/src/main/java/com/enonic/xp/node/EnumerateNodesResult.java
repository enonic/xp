package com.enonic.xp.node;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;

/**
 * One batch of a {@link NodeService#enumerate(EnumerateNodesParams)} enumeration: its entries and the position where the enumeration
 * continues. The entries of an enumeration bounded by {@link EnumerateNodesParams.Builder#modifiedBefore(Instant)} arrive oldest first;
 * those of an unbounded one in no specified order.
 *
 * @since 8.1.0
 */
@NullMarked
public final class EnumerateNodesResult
{
    private final ImmutableList<NodeEnumerationEntry> entries;

    @Nullable
    private final String cursor;

    private final int remaining;

    private EnumerateNodesResult( final Builder builder )
    {
        this.entries = builder.entries.build();
        this.cursor = builder.cursor;
        this.remaining = builder.remaining;
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The entries of this batch.
     */
    public List<NodeEnumerationEntry> getEntries()
    {
        return entries;
    }

    /**
     * How many entries are left of the enumeration, this batch included: the number of entries this batch holds plus the number every
     * batch after it will hold, {@link Integer#MAX_VALUE} at the most. The first batch of an enumeration therefore says how many nodes
     * the whole of it covers, before any of it has been walked.
     * <p>
     * The number is counted as this batch is answered. A node written below the parent, or deleted from it, after that moment is not
     * accounted for, and the next batch counts again — so a consumer that adds this to the entries it has consumed already follows the
     * size of the enumeration as it changes rather than holding on to the first answer.
     */
    public int getRemaining()
    {
        return remaining;
    }

    /**
     * Where the enumeration continues, or {@code null} where it is exhausted. Pass a non-null cursor unchanged to
     * {@link EnumerateNodesParams.Builder#cursor(String)} of the next call, and treat the enumeration as finished only when a batch
     * answers with {@code null} — whether a batch holds entries says nothing about whether it is the last.
     */
    public @Nullable String getCursor()
    {
        return cursor;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<NodeEnumerationEntry> entries = ImmutableList.builder();

        @Nullable
        private String cursor;

        private int remaining;

        private Builder()
        {
        }

        public Builder remaining( final int remaining )
        {
            this.remaining = remaining;
            return this;
        }

        public Builder addEntry( final NodeEnumerationEntry entry )
        {
            this.entries.add( entry );
            return this;
        }

        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        public EnumerateNodesResult build()
        {
            return new EnumerateNodesResult( this );
        }
    }
}
