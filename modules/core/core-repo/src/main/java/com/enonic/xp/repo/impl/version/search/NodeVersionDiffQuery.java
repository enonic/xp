package com.enonic.xp.repo.impl.version.search;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.AbstractQuery;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

public class NodeVersionDiffQuery
    extends AbstractQuery
{
    private final Branch source;

    private final Branch target;

    private final NodePath nodePath;

    private final NodePaths excludes;

    private NodeVersionDiffQuery( Builder builder )
    {
        super( builder );
        this.source = builder.source;
        this.target = builder.target;
        this.nodePath = builder.nodePath;
        this.excludes = builder.excludes.build();
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

    public NodePaths getExcludes()
    {
        return excludes;
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

        private final NodePaths.Builder excludes = NodePaths.create();


        public Builder()
        {
            super();
        }

        public Builder source( final Branch source )
        {
            this.source = source;
            return this;
        }

        public Builder target( final Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder excludes( final NodePaths excludes )
        {
            this.excludes.addNodePaths( excludes.getSet() );
            return this;
        }

        public NodeVersionDiffQuery build()
        {
            return new NodeVersionDiffQuery( this );
        }
    }
}

