package com.enonic.xp.node;

import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableList;

/**
 * One batch of a {@link NodeService#enumerate(EnumerateNodesParams)} enumeration: the entries the caller is permitted to read, in an
 * order that carries no meaning, and the position where the enumeration continues.
 *
 * @since 8.1.0
 */
@NullMarked
public final class EnumerateNodesResult
{
    private final ImmutableList<NodeEnumerationEntry> entries;

    @Nullable
    private final String cursor;

    private EnumerateNodesResult( final Builder builder )
    {
        this.entries = builder.entries.build();
        this.cursor = builder.cursor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The entries of this batch. A batch may hold no entries while the enumeration is not finished: entries the caller is not permitted
     * to read still advance the cursor.
     */
    public List<NodeEnumerationEntry> getEntries()
    {
        return entries;
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

        private Builder()
        {
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
