package com.enonic.xp.node;

@Deprecated
public final class RoutableNodeVersionId
{
    private final NodeId nodeId;

    private final NodeVersionId nodeVersionId;

    private RoutableNodeVersionId( final Builder builder )
    {
        nodeId = builder.nodeId;
        nodeVersionId = builder.nodeVersionId;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public static RoutableNodeVersionId from( NodeId nodeId, NodeVersionId nodeVersionId )
    {
        return create().nodeId( nodeId ).
            nodeVersionId( nodeVersionId ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodeVersionId nodeVersionId;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodeVersionId( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionId = nodeVersionId;
            return this;
        }

        public RoutableNodeVersionId build()
        {
            return new RoutableNodeVersionId( this );
        }
    }
}
