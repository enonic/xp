package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class DeleteNodeParams
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final RefreshMode refresh;

    private final DeleteNodeListener deleteNodeListener;

    private DeleteNodeParams( final Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.nodePath = builder.nodePath;
        this.refresh = builder.refresh;
        this.deleteNodeListener = builder.deleteNodeListener;
    }

    public static Builder create()
    {
        return new DeleteNodeParams.Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public DeleteNodeListener getDeleteNodeListener()
    {
        return deleteNodeListener;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodePath nodePath;

        private RefreshMode refresh;

        private DeleteNodeListener deleteNodeListener;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public Builder deleteNodeListener( final DeleteNodeListener deleteNodeListener )
        {
            this.deleteNodeListener = deleteNodeListener;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public DeleteNodeParams build()
        {
            Preconditions.checkArgument( this.nodePath != null ^ this.nodeId != null,
                                         "Either nodePath or nodeId must be specified, but not both" );
            return new DeleteNodeParams( this );
        }
    }
}
