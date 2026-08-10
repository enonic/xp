package com.enonic.xp.node;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for {@link NodeService#list(ListNodesParams)}.
 *
 * @since 8.1.0
 */
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
        private NodePath parentPath;

        private boolean recursive;

        private Builder()
        {
        }

        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        /**
         * Lists every descendant of the parent instead of the direct children only. Entries come back ordered by path either way.
         */
        public Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        public ListNodesParams build()
        {
            return new ListNodesParams( this );
        }
    }
}
