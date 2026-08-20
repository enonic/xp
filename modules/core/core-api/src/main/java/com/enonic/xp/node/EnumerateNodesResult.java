package com.enonic.xp.node;

import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;

/**
 * One batch of a {@link NodeService#enumerate(EnumerateNodesParams)} enumeration: its entries, in an order that carries no meaning,
 * and the position where the enumeration continues.
 *
 * @since 8.1.0
 */
@NullMarked
public final class EnumerateNodesResult
{
    private final ImmutableList<NodeEnumerationEntry> entries;

    @Nullable
    private final String cursor;

    private final long remaining;

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
     * How many entries this batch and every batch after it hold together — what is left of the enumeration, this batch included. The
     * index counts them while cutting the batch, so the first batch of an enumeration says how large the whole of it is, before any of
     * it has been walked.
     * <p>
     * Exact rather than estimated, since an enumeration answers with everything the subtree holds and filters nothing away. It is
     * counted afresh for every batch though, so a write ahead of the cursor moves it — a consumer adding it to what it has already
     * consumed sees the size of the enumeration correct itself rather than stay wrong.
     */
    public long getRemaining()
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

        private long remaining;

        private Builder()
        {
        }

        public Builder remaining( final long remaining )
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
