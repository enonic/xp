package com.enonic.wem.api.content;

import com.enonic.wem.api.index.ChildOrder;

public class FindContentByParentParams
{
    private final ContentPath parentPath;

    private final Integer size;

    private final Integer from;

    private final ChildOrder childOrder;

    private static final Integer DEFAULT_SIZE = 500;

    private FindContentByParentParams( Builder builder )
    {
        this.parentPath = builder.parentPath;
        this.size = builder.size;
        this.from = builder.from;
        this.childOrder = builder.childOrder;
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

    public ChildOrder getChildOrder()
    {
        return childOrder;
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

        private ChildOrder childOrder;

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
