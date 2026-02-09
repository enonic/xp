package com.enonic.xp.node;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
@NullMarked
public final class GetNodeVersionsParams
{
    private final NodeId nodeId;

    @Nullable
    private final String cursor;

    private final int size;

    private GetNodeVersionsParams( Builder builder )
    {
        nodeId = Objects.requireNonNull( builder.nodeId, "nodeId cannot be null" );
        cursor = builder.cursor;
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

    @Nullable
    public String getCursor()
    {
        return cursor;
    }

    public int getSize()
    {
        return size;
    }

    public static final class Builder
    {
        @Nullable
        private NodeId nodeId;

        @Nullable
        private String cursor;

        private int size = 10;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder cursor( final @Nullable String cursor )
        {
            this.cursor = cursor;
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
