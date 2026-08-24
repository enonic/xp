package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import static java.util.Objects.requireNonNull;


public final class ReorderChildNodeParams
{
    private final NodeId nodeId;

    private final NodeId moveBefore;

    private final String afterOrderKey;

    private final String beforeOrderKey;

    private ReorderChildNodeParams( Builder builder )
    {
        nodeId = builder.nodeId;
        moveBefore = builder.moveBefore;
        afterOrderKey = builder.afterOrderKey;
        beforeOrderKey = builder.beforeOrderKey;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeId getMoveBefore()
    {
        return moveBefore;
    }

    /**
     * The order key of the sibling shown directly above the drop point - the moved node lands after it. With
     * {@link #getBeforeOrderKey()} also set, the node lands between the two. Neither set means the top of the list.
     * Keys are the opaque strings read off the siblings; the placement minted from them is the server's own.
     */
    public String getAfterOrderKey()
    {
        return afterOrderKey;
    }

    /**
     * The order key of the sibling shown directly below the drop point - the moved node lands before it.
     */
    public String getBeforeOrderKey()
    {
        return beforeOrderKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodeId moveBefore;

        private String afterOrderKey;

        private String beforeOrderKey;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder moveBefore( NodeId moveBefore )
        {
            this.moveBefore = moveBefore;
            return this;
        }

        public Builder afterOrderKey( final String afterOrderKey )
        {
            this.afterOrderKey = afterOrderKey;
            return this;
        }

        public Builder beforeOrderKey( final String beforeOrderKey )
        {
            this.beforeOrderKey = beforeOrderKey;
            return this;
        }

        public ReorderChildNodeParams build()
        {
            requireNonNull( nodeId, "nodeId is required" );
            Preconditions.checkArgument( !nodeId.equals( moveBefore ), "nodeId and moveBefore must be different" );
            return new ReorderChildNodeParams( this );
        }
    }
}
