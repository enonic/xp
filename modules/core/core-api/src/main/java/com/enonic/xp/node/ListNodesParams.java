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

    private ListNodesParams( final Builder builder )
    {
        this.parentPath = requireNonNull( builder.parentPath, "parentPath is required" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodePath getParentPath()
    {
        return parentPath;
    }

    public static final class Builder
    {
        @Nullable
        private NodePath parentPath;

        private Builder()
        {
        }

        /**
         * Sets the node whose subtree is listed. Required.
         *
         * @param parentPath path of the parent node, {@link NodePath#ROOT} for the top level of the tree.
         */
        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
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
