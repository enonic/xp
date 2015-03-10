package com.enonic.xp.node;

import com.google.common.base.Preconditions;


public class FindNodesByQueryParams
{
    private final NodeQuery nodeQuery;

    private final boolean resolveHasChild;

    private FindNodesByQueryParams( Builder builder )
    {
        Preconditions.checkArgument( builder.nodeQuery != null, "nodeQuery must not be null" );
        nodeQuery = builder.nodeQuery;
        resolveHasChild = builder.resolveHasChild;
    }

    public NodeQuery getNodeQuery()
    {
        return nodeQuery;
    }

    public boolean isResolveHasChild()
    {
        return resolveHasChild;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeQuery nodeQuery;

        private boolean resolveHasChild = false;

        private Builder()
        {
        }

        public Builder nodeQuery( final NodeQuery nodeQuery )
        {
            this.nodeQuery = nodeQuery;
            return this;
        }

        public Builder resolveHasChild( boolean resolveHasChild )
        {
            this.resolveHasChild = resolveHasChild;
            return this;
        }

        public FindNodesByQueryParams build()
        {
            return new FindNodesByQueryParams( this );
        }
    }
}
