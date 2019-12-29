package com.enonic.xp.node;

import java.util.Set;
import java.util.function.Function;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.CompareStatus;

@PublicApi
public class SyncWorkResolverParams
{
    private Branch branch;

    private NodeId nodeId;

    private NodeIds excludedNodeIds;

    private final boolean includeChildren;

    private final boolean includeDependencies;

    private final Function<NodeIds, NodeIds> initialDiffFilter;

    private final Set<CompareStatus> statusesToStopDependenciesSearch;

    private SyncWorkResolverParams( Builder builder )
    {
        branch = builder.branch;
        nodeId = builder.nodeId;
        excludedNodeIds = builder.excludedNodeIds;
        includeChildren = builder.includeChildren;
        this.includeDependencies = builder.includeDependencies;
        this.initialDiffFilter = builder.initialDiffFilter;
        this.statusesToStopDependenciesSearch = builder.statusesToStopDependenciesSearch;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeIds getExcludedNodeIds()
    {
        return excludedNodeIds;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public boolean isIncludeDependencies()
    {
        return includeDependencies;
    }

    public Function<NodeIds, NodeIds> getInitialDiffFilter()
    {
        return initialDiffFilter;
    }

    public Set<CompareStatus> getStatusesToStopDependenciesSearch()
    {
        return statusesToStopDependenciesSearch;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Branch branch;

        private NodeId nodeId;

        private NodeIds excludedNodeIds;

        private boolean includeChildren;

        private boolean includeDependencies = true;

        private Set<CompareStatus> statusesToStopDependenciesSearch;

        private Function<NodeIds, NodeIds> initialDiffFilter;

        private Builder()
        {
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder excludedNodeIds( final NodeIds excludedNodeIds )
        {
            this.excludedNodeIds = excludedNodeIds;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder initialDiffFilter( final Function<NodeIds, NodeIds> initialDiffFilter )
        {
            this.initialDiffFilter = initialDiffFilter;
            return this;
        }

        public Builder statusesToStopDependenciesSearch( final Set<CompareStatus> statusesToStopDependenciesSearch )
        {
            this.statusesToStopDependenciesSearch = statusesToStopDependenciesSearch;
            return this;
        }

        public SyncWorkResolverParams build()
        {
            return new SyncWorkResolverParams( this );
        }
    }
}
