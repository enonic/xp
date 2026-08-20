package com.enonic.xp.node;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for {@link NodeService#list(ListNodesParams)}.
 *
 * @since 8.1.0
 */
@NullMarked
public final class ListNodesParams
{
    private final NodePath parentPath;

    private final boolean recursive;

    private final int batchSize;

    @Nullable
    private final String cursor;

    private ListNodesParams( final Builder builder )
    {
        this.parentPath = requireNonNull( builder.parentPath, "parentPath is required" );
        this.recursive = builder.recursive;
        if ( builder.batchSize < 0 )
        {
            throw new IllegalArgumentException( "batchSize cannot be negative" );
        }
        if ( builder.cursor != null && builder.batchSize == 0 )
        {
            throw new IllegalArgumentException( "cursor expects a batchSize" );
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

    public boolean isRecursive()
    {
        return recursive;
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

        private boolean recursive;

        private int batchSize;

        @Nullable
        private String cursor;

        private Builder()
        {
        }

        /**
         * Sets the node whose children are listed. Required.
         *
         * @param parentPath path of the parent node, {@link NodePath#ROOT} for the top level of the tree.
         */
        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        /**
         * Lists every descendant of the parent rather than the direct children only. Narrowing to the direct children is a semantic
         * choice, never a cheaper one: the storage index holds no parent field, so a non-recursive listing walks the same subtree
         * and drops the deeper levels afterward, costing as much as the recursive listing or more.
         */
        public Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        /**
         * Bounds a single call to at most the given number of entries, turning the listing into a sequence of batches. A batch that may
         * be followed by another carries a {@link ListNodesResult#getCursor() cursor}, to be passed to {@link #cursor(String)} of the
         * next call.
         * <p>
         * Without a batch size the whole listing is returned at once, so a caller expecting many entries is advised to set one and
         * consume the listing batch by batch. A batched listing arrives in an order that carries no meaning, and in return a node moved
         * within the listing while it is consumed is still observed exactly once.
         *
         * @param batchSize the most entries a single call returns; a positive number, or {@code 0}, the default, for the whole listing
         * at once.
         */
        public Builder batchSize( final int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        /**
         * Continues a batched listing from where the previous batch stopped. Pass the {@link ListNodesResult#getCursor() cursor} of that
         * batch unchanged, or {@code null} for the first call. The listing must be continued with the same parent path, recursion and
         * batch size for the sequence of batches to add up to the whole listing.
         */
        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        /**
         * @throws NullPointerException where no parent path has been set.
         * @throws IllegalArgumentException where a batch size is negative, or a cursor is given without a batch size.
         */
        public ListNodesParams build()
        {
            return new ListNodesParams( this );
        }
    }
}
