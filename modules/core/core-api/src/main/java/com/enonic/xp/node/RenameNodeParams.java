package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RenameNodeParams
{
    private final NodeId nodeId;

    private final NodeName nodeName;

    private final NodePath parentPath;

    private final MoveNodeListener moveListener;

    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private RenameNodeParams( final Builder builder )
    {
        this.nodeId = Objects.requireNonNull( builder.nodeId, "nodeId is required" );
        this.nodeName = builder.nodeName;
        this.parentPath = builder.parentPath;
        this.moveListener = Objects.requireNonNullElse( builder.moveListener, count -> {
        } );
        this.processor = Objects.requireNonNullElse( builder.processor, ( n, p ) -> n);
        this.refresh = builder.refresh;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeName getNewNodeName()
    {
        return nodeName;
    }

    public NodePath getNewParentPath()
    {
        return parentPath;
    }

    public MoveNodeListener getMoveListener()
    {
        return moveListener;
    }

    public NodeDataProcessor getProcessor()
    {
        return processor;
    }

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodeName nodeName;

        private NodePath parentPath;

        private MoveNodeListener moveListener;

        private NodeDataProcessor processor;

        private RefreshMode refresh;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodeName( final NodeName nodeName )
        {
            this.nodeName = nodeName;
            return this;
        }

        public Builder parentPath( NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder moveListener( MoveNodeListener moveListener )
        {
            this.moveListener = moveListener;
            return this;
        }

        public Builder processor( final NodeDataProcessor processor )
        {
            this.processor = processor;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public RenameNodeParams build()
        {
            Preconditions.checkArgument( this.nodeName != null || this.parentPath != null , "nodeName or parentPath is required" );
            return new RenameNodeParams( this );
        }
    }
}
