package com.enonic.wem.repo.internal.version;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.AbstractQuery;
import com.enonic.xp.node.NodePath;

public class NodeVersionDiffQuery
    extends AbstractQuery
{
    private final Branch source;

    private final Branch target;

    private final NodePath nodePath;

    private NodeVersionDiffQuery( Builder builder )
    {
        super( builder );
        source = builder.source;
        target = builder.target;
        nodePath = builder.nodePath;
    }

    public Branch getSource()
    {
        return source;
    }

    public Branch getTarget()
    {
        return target;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private Branch source;

        private Branch target;

        private NodePath nodePath;

        public Builder()
        {
            super();
        }

        public Builder source( Branch source )
        {
            this.source = source;
            return this;
        }

        public Builder target( Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder nodePath( NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public NodeVersionDiffQuery build()
        {
            return new NodeVersionDiffQuery( this );
        }
    }
}

