package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class NodeQuery
    extends AbstractQuery
{
    public static final int ALL_RESULTS_SIZE_FLAG = -1;

    private final NodePath parent;

    private final NodePath path;

    private final boolean accurateScoring;

    private final boolean withPath;

    private NodeQuery( final Builder builder )
    {
        super( builder );
        this.parent = builder.parent;
        this.path = builder.path;
        this.accurateScoring = builder.accurateScoring;
        this.withPath = builder.withPath;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public NodePath getPath()
    {
        return path;
    }

    public boolean isAccurateScoring()
    {
        return accurateScoring;
    }

    public boolean isWithPath()
    {
        return withPath;
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

        private boolean accurateScoring = false;

        private boolean withPath = false;

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

        public Builder accurateScoring( final boolean accurateScoring )
        {
            this.accurateScoring = accurateScoring;
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
