package com.enonic.xp.repo.impl.version.search;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.node.AbstractQuery;
import com.enonic.xp.node.NodePath;

public class NodeVersionDiffQuery
    extends AbstractQuery
{
    private final BranchId source;

    private final BranchId target;

    private final NodePath nodePath;

    private final ExcludeEntries excludes;

    private NodeVersionDiffQuery( Builder builder )
    {
        super( builder );
        this.source = builder.source;
        this.target = builder.target;
        this.nodePath = builder.nodePath;
        this.excludes = builder.excludes;
    }

    public BranchId getSource()
    {
        return source;
    }

    public BranchId getTarget()
    {
        return target;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public ExcludeEntries getExcludes()
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
        private BranchId source;

        private BranchId target;

        private NodePath nodePath;

        private ExcludeEntries excludes = ExcludeEntries.empty();


        public Builder()
        {
            super();
        }

        public Builder source( final BranchId source )
        {
            this.source = source;
            return this;
        }

        public Builder target( final BranchId target )
        {
            this.target = target;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder excludes( final ExcludeEntries excludes )
        {
            this.excludes = excludes;
            return this;
        }

        public NodeVersionDiffQuery build()
        {
            return new NodeVersionDiffQuery( this );
        }
    }
}

