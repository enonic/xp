package com.enonic.xp.node;


import java.util.Map;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;

public class PatchVersionResult
{
    private final NodeVersionKey nodeVersionKey;

    private final NodePath nodePath;

    private final Map<Branch, NodeBranchEntry> nodeBranchEntries;

    private PatchVersionResult( final Builder builder )
    {
        this.nodeVersionKey = builder.nodeVersionKey;
        this.nodePath = builder.nodePath;
        this.nodeBranchEntries = builder.nodeBranchEntries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeVersionKey getNodeVersionKey()
    {
        return nodeVersionKey;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public Map<Branch, NodeBranchEntry> getNodeBranchEntries()
    {
        return nodeBranchEntries;
    }

    public static final class Builder
    {
        private NodeVersionKey nodeVersionKey;

        private NodePath nodePath;

        private Map<Branch, NodeBranchEntry> nodeBranchEntries;

        private Builder()
        {
        }

        public Builder nodeVersionKey( final NodeVersionKey nodeVersionKey )
        {
            this.nodeVersionKey = nodeVersionKey;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder nodeBranchEntries( final Map<Branch, NodeBranchEntry> nodeBranchEntries )
        {
            this.nodeBranchEntries = nodeBranchEntries;
            return this;
        }

        public PatchVersionResult build()
        {
            return new PatchVersionResult( this );
        }
    }


}
