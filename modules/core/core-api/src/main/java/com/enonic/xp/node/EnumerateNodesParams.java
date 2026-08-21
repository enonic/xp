package com.enonic.xp.node;

import java.time.Instant;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for {@link NodeService#enumerate(EnumerateNodesParams)}.
 *
 * @since 8.1.0
 */
@NullMarked
public final class EnumerateNodesParams
{
    /**
     * The largest batch an enumeration may ask for, and what it asks for unless told otherwise.
     */
    private static final int MAX_BATCH_SIZE = 10_000;

    private final NodePath parentPath;

    private final int batchSize;

    @Nullable
    private final Instant modifiedBefore;

    @Nullable
    private final String cursor;

    private EnumerateNodesParams( final Builder builder )
    {
        this.parentPath = requireNonNull( builder.parentPath, "parentPath is required" );
        if ( builder.batchSize <= 0 )
        {
            throw new IllegalArgumentException( "batchSize must be positive" );
        }
        if ( builder.batchSize > MAX_BATCH_SIZE )
        {
            throw new IllegalArgumentException( "batchSize cannot exceed " + MAX_BATCH_SIZE );
        }
        this.batchSize = builder.batchSize;
        this.modifiedBefore = builder.modifiedBefore;
        this.cursor = builder.cursor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodePath getParentPath()
    {
        return parentPath;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public @Nullable Instant getModifiedBefore()
    {
        return modifiedBefore;
    }

    public @Nullable String getCursor()
    {
        return cursor;
    }

    public static final class Builder
    {
        @Nullable
        private NodePath parentPath;

        private int batchSize = MAX_BATCH_SIZE;

        @Nullable
        private Instant modifiedBefore;

        @Nullable
        private String cursor;

        private Builder()
        {
        }

        /**
         * Sets the node whose subtree is enumerated. Required.
         *
         * @param parentPath path of the parent node, {@link NodePath#ROOT} for the top level of the tree.
         */
        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

         /**
         * The most entries a single call returns: at least 1, at most 10 000, and 10 000 where it is not set. A walk that hands every
         * batch on and keeps nothing of it has no reason to ask for less; set it lower only where fewer entries are known to settle the
         * question being asked.
         *
         * @param batchSize positive and no larger than 10 000.
         */
        public Builder batchSize( final int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        /**
         * Bounds the enumeration to the nodes whose timestamp falls strictly before the given moment. Each node is judged by the
         * timestamp it carries when the batch that would hold it is answered, not by the one it carried when the enumeration began. A
         * bounded enumeration arrives oldest first.
         */
        public Builder modifiedBefore( final @Nullable Instant modifiedBefore )
        {
            this.modifiedBefore = modifiedBefore;
            return this;
        }

        /**
         * Continues an enumeration from where the previous batch stopped. Pass the {@link EnumerateNodesResult#getCursor() cursor} of
         * that batch unchanged, or {@code null} for the first call. The enumeration must be continued with the same parent path, batch
         * size and bound for the sequence of batches to add up to the whole subtree.
         */
        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        /**
         * @throws NullPointerException where no parent path has been set.
         * @throws IllegalArgumentException where the batch size is not positive, or larger than 10 000.
         */
        public EnumerateNodesParams build()
        {
            return new EnumerateNodesParams( this );
        }
    }
}
