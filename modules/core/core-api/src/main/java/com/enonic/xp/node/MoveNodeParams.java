package com.enonic.xp.node;

public class MoveNodeParams
{
    private final NodeId nodeId;

    private final NodePath parentNodePath;

    private final MoveNodeListener moveListener;

    private final NodeDataProcessor processor;

    private final RefreshMode refresh;

    private MoveNodeParams( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.parentNodePath = builder.parentNodePath;
        this.moveListener = builder.moveListener;
        this.processor = builder.processor;
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

    public NodePath getParentNodePath()
    {
        return parentNodePath;
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

        private NodePath parentNodePath;

        private MoveNodeListener moveListener;

        private NodeDataProcessor processor = ( n, p ) -> n;

        private RefreshMode refresh;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder parentNodePath( NodePath parentNodePath )
        {
            this.parentNodePath = parentNodePath;
            return this;
        }

        public Builder moveListener( MoveNodeListener moveListener )
        {
            this.moveListener = moveListener;
            return this;
        }

        public Builder processor( NodeDataProcessor processor )
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
            return new MoveNodeParams( this );
        }
    }
}
