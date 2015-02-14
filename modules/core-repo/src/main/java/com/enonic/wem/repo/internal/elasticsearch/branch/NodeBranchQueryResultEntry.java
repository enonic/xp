package com.enonic.wem.repo.internal.elasticsearch.branch;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionId;

public class NodeBranchQueryResultEntry
{
    private final NodeId nodeId;

    private final NodeVersionId nodeVersionId;

    private NodeBranchQueryResultEntry( Builder builder )
    {
        nodeId = builder.nodeId;
        nodeVersionId = builder.nodeVersionId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }


    public static final class Builder
    {
        private NodeId nodeId;

        private NodeVersionId nodeVersionId;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodeVersionId( NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public NodeBranchQueryResultEntry build()
        {
            return new NodeBranchQueryResultEntry( this );
        }
    }
}
