package com.enonic.wem.api.content;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class GetContentByParentParams
{
    private final ContentPath parentPath;

    private final Integer size;

    private final Integer from;

    private final ImmutableSet<FieldSort> sorting;

    private static final Integer DEFAULT_SIZE = 500;

    private GetContentByParentParams( Builder builder )
    {
        parentPath = builder.parentPath;
        size = builder.size;
        from = builder.from;
        sorting = ImmutableSet.copyOf( builder.sorting );
    }

    public ContentPath getParentPath()
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

    public static Integer getDefaultSize()
    {
        return DEFAULT_SIZE;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentPath parentPath;

        private Integer size = DEFAULT_SIZE;

        private Integer from = 0;

        private Set<FieldSort> sorting = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder parentPath( ContentPath parentPath )
        {
            this.parentPath = parentPath;
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

        public Builder addSort( final String fieldName, final Direction direction )
        {
            this.sorting.add( new FieldSort( fieldName, direction ) );
            return this;
        }

        public GetContentByParentParams build()
        {
            return new GetContentByParentParams( this );
        }
    }
}
