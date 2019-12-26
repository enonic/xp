package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.query.filter.Filters;

@PublicApi
public class FindNodesByParentParams
{
    private final NodePath parentPath;

    private final NodeId parentId;

    private final Filters queryFilters;

    private final Integer size;

    private final Integer from;

    private final ChildOrder childOrder;

    private final boolean countOnly;

    private final boolean recursive;

    private FindNodesByParentParams( Builder builder )
    {
        Preconditions.checkArgument( builder.parentPath == null || builder.parentId == null,
                                     "expected either parentPath or parentId, but not both" );
        parentPath = builder.parentPath;
        parentId = builder.parentId;
        queryFilters = builder.queryFilters == null ? Filters.from() : builder.queryFilters;
        size = builder.size;
        from = builder.from;
        childOrder = builder.childOrder;
        countOnly = builder.countOnly;
        recursive = builder.recursive;
    }

    public NodePath getParentPath()
    {
        return parentPath;
    }

    public NodeId getParentId()
    {
        return parentId;
    }

    public Filters getQueryFilters()
    {
        return queryFilters;
    }

    public Integer getSize()
    {
        return size;
    }

    public Integer getFrom()
    {
        return from;
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public boolean isCountOnly()
    {
        return countOnly;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath parentPath;

        private NodeId parentId;

        private Filters queryFilters;

        private Integer size = -1;

        private Integer from = 0;

        private ChildOrder childOrder;

        private boolean countOnly = false;

        private boolean recursive = false;

        private Builder()
        {
        }

        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder parentId( NodeId parentId )
        {
            this.parentId = parentId;
            return this;
        }


        public Builder queryFilters( Filters queryFilters )
        {
            this.queryFilters = queryFilters;
            return this;
        }

        public Builder size( final Integer size )
        {
            this.size = size;
            return this;
        }

        public Builder from( final Integer from )
        {
            this.from = from;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder countOnly( final boolean countOnly )
        {
            this.countOnly = countOnly;
            return this;
        }

        public Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( parentId != null || parentPath != null, "Either parent-path or parent-id must be set" );
        }

        public FindNodesByParentParams build()
        {
            validate();
            return new FindNodesByParentParams( this );
        }
    }
}
