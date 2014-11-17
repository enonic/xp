package com.enonic.wem.api.node;

import com.enonic.wem.api.index.ChildOrder;

public class FindNodesByParentParams
{
    private final NodePath parentPath;

    private final Integer size;

    private final Integer from;

    private final ChildOrder childOrder;

    private static final Integer DEFAULT_SIZE = 500;

    private FindNodesByParentParams( Builder builder )
    {
        parentPath = builder.parentPath;
        size = builder.size;
        from = builder.from;
        childOrder = builder.childOrder;
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
        private NodePath parentPath;

        private Integer size = 10;

        private Integer from = 0;

        private ChildOrder childOrder;

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

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public FindNodesByParentParams build()
        {
            return new FindNodesByParentParams( this );
        }
    }
}
