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

    private ListNodesParams( final Builder builder )
    {
        this.parentPath = requireNonNull( builder.parentPath, "parentPath is required" );
        this.recursive = builder.recursive;
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

    public static final class Builder
    {
        @Nullable
        private NodePath parentPath;

        private boolean recursive;

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
         * Lists every descendant of the parent rather than the direct children only. Entries are ordered by path in either case.
         */
        public Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        /**
         * @throws NullPointerException where no parent path has been set.
         */
        public ListNodesParams build()
        {
            return new ListNodesParams( this );
        }
    }
}
