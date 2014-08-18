package com.enonic.wem.api.entity;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.content.FieldSort;

public class FindNodesByParentParams
{
    private final NodePath parentPath;

    private final Integer size;

    private final Integer from;

    private final ImmutableSet<FieldSort> sorting;

    private static final Integer DEFAULT_SIZE = 500;

    private FindNodesByParentParams( Builder builder )
    {
        parentPath = builder.parentPath;
        size = builder.size;
        from = builder.from;
        sorting = ImmutableSet.copyOf( builder.sorting );
    }

    public NodePath getParentPath()
    {
        return parentPath;
    }

    public Integer getSize()
    {
        return size;
    }

    public Integer getFrom()
    {
        return from;
    }

    public ImmutableSet<FieldSort> getSorting()
    {
        return sorting;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodePath parentPath;

        private Integer size;

        private Integer from;

        private Set<FieldSort> sorting;

        private Builder()
        {
        }

        public Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
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

        public Builder sorting( final Set<FieldSort> sorting )
        {
            this.sorting = sorting;
            return this;
        }

        public FindNodesByParentParams build()
        {
            return new FindNodesByParentParams( this );
        }
    }
}
