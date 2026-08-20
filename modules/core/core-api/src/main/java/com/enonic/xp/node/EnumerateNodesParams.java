package com.enonic.xp.node;

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
    private final NodePath parentPath;

    private final int batchSize;

    @Nullable
    private final String cursor;

    private EnumerateNodesParams( final Builder builder )
    {
        this.parentPath = requireNonNull( builder.parentPath, "parentPath is required" );
        if ( builder.batchSize <= 0 )
        {
            throw new IllegalArgumentException( "batchSize must be positive" );
        }
        this.batchSize = builder.batchSize;
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

    public @Nullable String getCursor()
    {
        return cursor;
    }

    public static final class Builder
    {
        @Nullable
        private NodePath parentPath;

        private int batchSize;

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
         * The most entries a single call returns. Required, positive.
         */
        public Builder batchSize( final int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        /**
         * Continues an enumeration from where the previous batch stopped. Pass the {@link EnumerateNodesResult#getCursor() cursor} of
         * that batch unchanged, or {@code null} for the first call. The enumeration must be continued with the same parent path and
         * batch size for the sequence of batches to add up to the whole subtree.
         */
        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        /**
         * @throws NullPointerException where no parent path has been set.
         * @throws IllegalArgumentException where the batch size is not positive.
         */
        public EnumerateNodesParams build()
        {
            return new EnumerateNodesParams( this );
        }
    }
}
