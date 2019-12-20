package com.enonic.xp.repo.impl.version.search;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.AbstractQuery;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.repo.impl.version.TestQueryType;

public class NodeVersionDiffQuery
    extends AbstractQuery
{
    private final Branch source;

    private final Branch target;

    private final NodePath nodePath;

    private final ExcludeEntries excludes;

    private final int versionsSize;

    private boolean deleted;

    private TestQueryType testQueryType;

    private NodeVersionDiffQuery( Builder builder )
    {
        super( builder );
        this.source = builder.source;
        this.target = builder.target;
        this.nodePath = builder.nodePath;
        this.excludes = builder.excludes;
        this.versionsSize = builder.versionsSize;
        this.deleted = builder.deleted;
        this.testQueryType = builder.testQueryType;
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

    public ExcludeEntries getExcludes()
    {
        return excludes;
    }

    public int getVersionsSize()
    {
        return versionsSize;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( final boolean deleted )
    {
        this.deleted = deleted;
    }

    public void setSearchMode( final SearchMode mode )
    {
        this.searchMode = mode;
    }

    public TestQueryType getTestQueryType()
    {
        return testQueryType;
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

        private ExcludeEntries excludes = ExcludeEntries.empty();

        private int versionsSize = 0;

        private boolean deleted = false;

        private TestQueryType testQueryType;

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

        public Builder excludes( final ExcludeEntries excludes )
        {
            this.excludes = excludes;
            return this;
        }

        public Builder versionsSize( final int size )
        {
            this.versionsSize = size;
            return this;
        }

        public Builder deleted( final boolean deleted )
        {
            this.deleted = deleted;
            return this;
        }

        public Builder testQueryType( final TestQueryType testQueryType )
        {
            this.testQueryType = testQueryType;
            return this;
        }

        public NodeVersionDiffQuery build()
        {
            return new NodeVersionDiffQuery( this );
        }
    }
}

