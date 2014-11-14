package com.enonic.wem.repo;

public class GetNodeVersionsParams
{
    private final NodeId nodeId;

    private final int from;

    private final int size;

    private GetNodeVersionsParams( Builder builder )
    {
        nodeId = builder.nodeId;
        from = builder.from;
        size = builder.size;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private int from;

        private int size;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder from( int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( int size )
        {
            this.size = size;
            return this;
        }

        public GetNodeVersionsParams build()
        {
            return new GetNodeVersionsParams( this );
        }
    }
}
