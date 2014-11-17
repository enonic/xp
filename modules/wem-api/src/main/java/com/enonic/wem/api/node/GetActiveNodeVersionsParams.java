package com.enonic.wem.api.node;

import com.enonic.wem.api.workspace.Workspaces;

public class GetActiveNodeVersionsParams
{
    private final NodeId nodeId;

    private final Workspaces workspaces;

    private GetActiveNodeVersionsParams( Builder builder )
    {
        nodeId = builder.nodeId;
        workspaces = builder.workspaces;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public Workspaces getWorkspaces()
    {
        return workspaces;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private Workspaces workspaces;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder workspaces( final Workspaces workspaces )
        {
            this.workspaces = workspaces;
            return this;
        }

        public GetActiveNodeVersionsParams build()
        {
            return new GetActiveNodeVersionsParams( this );
        }
    }
}
