package com.enonic.wem.core.entity.query;


import com.enonic.wem.core.entity.NodePath;

public class NodeQuery
    extends EntityQuery
{
    private final NodePath parent;

    private final NodePath path;

    private NodeQuery( final Builder builder )
    {
        super( builder );
        this.parent = builder.parent;
        this.path = builder.path;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public NodePath getPath()
    {
        return path;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends EntityQuery.Builder<Builder>
    {
        private NodePath parent;

        private NodePath path;

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder path( final NodePath path )
        {
            this.path = path;
            return this;
        }

        @Override
        public NodeQuery build()
        {
            return new NodeQuery( this );
        }
    }

}
