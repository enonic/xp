package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class MoveNodeParams
{
    private final NodeId nodeId;

    private final NodeName newName;

    private final NodePath newParentPath;

    private final Attributes versionAttributes;

    private final MoveNodeListener moveListener;

    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private MoveNodeParams( final Builder builder )
    {
        this.nodeId = Objects.requireNonNull( builder.nodeId, "nodeId is required" );
        this.newName = builder.newName;
        this.newParentPath = builder.newParentPath;
        this.versionAttributes = builder.versionAttributes;
        this.moveListener = builder.moveListener;
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
        return newName;
    }

    public NodePath getNewParentPath()
    {
        return newParentPath;
    }

    public Attributes getVersionAttributes()
    {
        return versionAttributes;
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

        private NodeName newName;

        private NodePath newParentPath;

        private Attributes versionAttributes;

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

        public Builder newName( final NodeName nodeName )
        {
            this.newName = nodeName;
            return this;
        }

        public Builder newParentPath( final NodePath parentPath )
        {
            this.newParentPath = parentPath;
            return this;
        }

        public Builder versionAttributes( final Attributes versionAttributes )
        {
            this.versionAttributes = versionAttributes;
            return this;
        }

        public Builder moveListener( final MoveNodeListener moveListener )
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

        public MoveNodeParams build()
        {
            Preconditions.checkArgument( this.newName != null || this.newParentPath != null , "nodeName or parentPath is required" );
            return new MoveNodeParams( this );
        }
    }
}
