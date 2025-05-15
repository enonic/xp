package com.enonic.xp.node;

public final class LoadNodeParams
{
    private final Node node;

    private final NodeCommitId nodeCommitId;

    private LoadNodeParams( final Builder builder )
    {
        node = builder.node;
        nodeCommitId = builder.nodeCommitId;
    }

    public Node getNode()
    {
        return node;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Node node;

        private NodeCommitId nodeCommitId;

        private Builder()
        {
        }

        public Builder node( final Node val )
        {
            node = val;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public LoadNodeParams build()
        {
            return new LoadNodeParams( this );
        }
    }
}
