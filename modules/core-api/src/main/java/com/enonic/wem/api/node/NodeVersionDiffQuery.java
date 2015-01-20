package com.enonic.wem.api.node;

import com.enonic.wem.api.workspace.Workspace;

public class NodeVersionDiffQuery
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Workspace source;

    private final Workspace target;

    private final int size;

    private final int from;

    private NodeVersionDiffQuery( Builder builder )
    {
        nodeId = builder.nodeId;
        nodePath = builder.nodePath;
        source = builder.source;
        target = builder.target;
        size = builder.size;
        from = builder.from;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Workspace getSource()
    {
        return source;
    }

    public Workspace getTarget()
    {
        return target;
    }

    public int getSize()
    {
        return size;
    }

    public int getFrom()
    {
        return from;
    }

    public NodePath getNodePath()
    {
        return nodePath;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodePath nodePath;

        private Workspace source;

        private Workspace target;

        private int size = -1;

        private int from = 0;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder source( final Workspace source )
        {
            this.source = source;
            return this;
        }

        public Builder target( final Workspace target )
        {
            this.target = target;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public NodeVersionDiffQuery build()
        {
            return new NodeVersionDiffQuery( this );
        }
    }
}
