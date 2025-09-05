package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NodeQuery
    extends AbstractQuery
{
    public static final int ALL_RESULTS_SIZE_FLAG = -1;

    private final NodePath parent;

    private final boolean withPath;

    private NodeQuery( final Builder builder )
    {
        super( builder );
        this.parent = builder.parent;
        this.withPath = builder.withPath;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public boolean isWithPath()
    {
        return withPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private NodePath parent;

        private boolean withPath = false;

        private Builder()
        {
            super();
        }

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder withPath( final boolean withPath )
        {
            this.withPath = withPath;
            return this;
        }

        public NodeQuery build()
        {
            return new NodeQuery( this );
        }
    }

}
