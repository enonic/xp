package com.enonic.wem.core.version;

import com.enonic.wem.repo.NodeId;

public class GetVersionsQuery
{
    private final Integer from;

    private final Integer size;

    private final NodeId nodeId;

    private GetVersionsQuery( Builder builder )
    {
        from = builder.from;
        size = builder.size;
        nodeId = builder.nodeId;
    }

    public Integer getFrom()
    {
        return from;
    }

    public Integer getSize()
    {
        return size;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Integer from = 0;

        private Integer size = 10;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder from( Integer from )
        {
            this.from = from;
            return this;
        }

        public Builder size( Integer size )
        {
            this.size = size;
            return this;
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public GetVersionsQuery build()
        {
            return new GetVersionsQuery( this );
        }
    }
}
