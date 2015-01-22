package com.enonic.wem.api.node;

import com.enonic.wem.api.workspace.Workspace;

public class SyncWorkResolverParams
{
    private Workspace workspace;

    private NodeId nodeId;

    private boolean includeChildren;

    private SyncWorkResolverParams( Builder builder )
    {
        workspace = builder.workspace;
        nodeId = builder.nodeId;
        includeChildren = builder.includeChildren;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Workspace workspace;

        private NodeId nodeId;

        private boolean includeChildren;

        private Builder()
        {
        }

        public Builder workspace( Workspace workspace )
        {
            this.workspace = workspace;
            return this;
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder includeChildren( boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public SyncWorkResolverParams build()
        {
            return new SyncWorkResolverParams( this );
        }
    }
}
