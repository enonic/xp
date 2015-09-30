package com.enonic.xp.node;


import com.google.common.annotations.Beta;

import com.enonic.xp.security.Principals;

@Beta
public class NodeQuery
    extends AbstractQuery
{
    private final NodePath parent;

    private final NodePath path;

    private final Principals principals;

    private NodeQuery( final Builder builder )
    {
        super( builder );
        this.parent = builder.parent;
        this.path = builder.path;
        this.principals = builder.principals;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public NodePath getPath()
    {
        return path;
    }

    public Principals getPrincipals()
    {
        return principals;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private NodePath parent;

        private NodePath path;

        private Principals principals;

        public Builder()
        {
            super();
        }

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

        public Builder principals( final Principals principals )
        {
            this.principals = principals;
            return this;
        }

        public NodeQuery build()
        {
            return new NodeQuery( this );
        }
    }

}
