package com.enonic.xp.node;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class SyncWorkResolverParams
{
    private Branch branch;

    private NodeId nodeId;

    private NodeIds excludedNodeIds;

    private final boolean includeChildren;

    private final boolean includeDependencies;

    private SyncWorkResolverParams( Builder builder )
    {
        branch = builder.branch;
        nodeId = builder.nodeId;
        excludedNodeIds = builder.excludedNodeIds;
        includeChildren = builder.includeChildren;
        this.includeDependencies = builder.includeDependencies;
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

        public SyncWorkResolverParams build()
        {
            return new SyncWorkResolverParams( this );
        }
    }
}
