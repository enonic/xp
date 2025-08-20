package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;

@PublicApi
public final class FindContentByParentParams
{
    private final ContentPath parentPath;

    private final ContentId parentId;

    private final Filters queryFilters;

    private final Integer size;

    private final Integer from;

    private final ChildOrder childOrder;

    private final Boolean recursive;

    private static final Integer DEFAULT_SIZE = 500;

    private FindContentByParentParams( Builder builder )
    {
        Preconditions.checkArgument( builder.parentPath == null || builder.parentId == null,
                                     "expected either parentPath or parentId, but not both" );
        this.parentPath = builder.parentPath;
        this.parentId = builder.parentId;
        this.queryFilters = builder.queryFilters.build();
        this.size = builder.size;
        this.from = builder.from;
        this.childOrder = builder.childOrder;
        this.recursive = builder.recursive;
    }

    public ContentPath getParentPath()
    {
        return parentPath;
    }

    public ContentId getParentId()
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

    public Boolean isRecursive()
    {
        return recursive;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentPath parentPath;

        private ContentId parentId;

        private final Filters.Builder queryFilters = Filters.create();

        private Integer size = DEFAULT_SIZE;

        private Integer from = 0;

        private ChildOrder childOrder;

        private Boolean recursive = false;

        private Builder()
        {
        }

        public Builder parentPath( ContentPath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder parentId( ContentId parentId )
        {
            this.parentId = parentId;
            return this;
        }

        public Builder queryFilter( Filter queryFilter )
        {
            this.queryFilters.add( queryFilter );
            return this;
        }

        public Builder size( Integer size )
        {
            this.size = size;
            return this;
        }

        public Builder from( Integer from )
        {
            this.from = from;
            return this;
        }

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder recursive( final Boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        public FindContentByParentParams build()
        {
            return new FindContentByParentParams( this );
        }
    }
}
