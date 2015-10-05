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

    private final boolean accurateScoring;

    private NodeQuery( final Builder builder )
    {
        super( builder );
        this.parent = builder.parent;
        this.path = builder.path;
        this.principals = builder.principals;
        this.accurateScoring = builder.accurateScoring;
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

    public boolean isAccurateScoring()
    {
        return accurateScoring;
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

        private boolean accurateScoring = false;

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

        public Builder accurateScoring( final boolean accurateScoring )
        {
            this.accurateScoring = accurateScoring;
            return this;
        }

        public NodeQuery build()
        {
            return new NodeQuery( this );
        }
    }

}
