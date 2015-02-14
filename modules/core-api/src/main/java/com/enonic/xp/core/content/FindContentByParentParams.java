package com.enonic.xp.core.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.index.ChildOrder;

public final class FindContentByParentParams
{
    private final ContentPath parentPath;

    private final ContentId parentId;

    private final Integer size;

    private final Integer from;

    private final ChildOrder childOrder;

    private static final Integer DEFAULT_SIZE = 500;

    private FindContentByParentParams( Builder builder )
    {
        Preconditions.checkArgument( builder.parentPath == null || builder.parentId == null,
                                     "expected either parentPath or parentId, but not both" );
        if ( builder.parentPath != null )
        {
            Preconditions.checkArgument( builder.parentPath.isAbsolute(), "parentPath must be absolute: " + builder.parentPath );
        }
        this.parentPath = builder.parentPath;
        this.parentId = builder.parentId;
        this.size = builder.size;
        this.from = builder.from;
        this.childOrder = builder.childOrder;
    }

    public ContentPath getParentPath()
    {
        return parentPath;
    }

    public ContentId getParentId()
    {
        return parentId;
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof FindContentByParentParams ) )
        {
            return false;
        }
        final FindContentByParentParams that = (FindContentByParentParams) o;
        return Objects.equals( this.parentPath, that.parentPath ) &&
            Objects.equals( this.parentId, that.parentId ) &&
            Objects.equals( this.size, that.size ) &&
            Objects.equals( this.from, that.from ) &&
            Objects.equals( this.childOrder, that.childOrder );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( parentPath, parentId, size, from, childOrder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentPath parentPath;

        private ContentId parentId;

        private Integer size = DEFAULT_SIZE;

        private Integer from = 0;

        private ChildOrder childOrder;

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

        public FindContentByParentParams build()
        {
            return new FindContentByParentParams( this );
        }
    }
}
