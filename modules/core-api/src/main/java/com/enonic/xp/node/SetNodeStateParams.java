package com.enonic.xp.node;

public class SetNodeStateParams
{
    private final NodeId nodeId;

    private final NodeState nodeState;

    private final boolean recursive;

    private SetNodeStateParams( final Builder builder )
    {
        nodeId = builder.nodeId;
        nodeState = builder.nodeState;
        recursive = builder.recursive;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public NodeState getNodeState()
    {
        return nodeState;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodeId nodeId;

        private NodeState nodeState;

        private boolean recursive = false;

        private Builder()
        {
        }

        public Builder nodeId( NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodeState( NodeState nodeState )
        {
            this.nodeState = nodeState;
            return this;
        }

        public Builder recursive( boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        public SetNodeStateParams build()
        {
            return new SetNodeStateParams( this );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final SetNodeStateParams that = (SetNodeStateParams) o;

        if ( recursive != that.recursive )
        {
            return false;
        }
        if ( nodeId != null ? !nodeId.equals( that.nodeId ) : that.nodeId != null )
        {
            return false;
        }
        return nodeState == that.nodeState;

    }

    @Override
    public int hashCode()
    {
        int result = nodeId != null ? nodeId.hashCode() : 0;
        result = 31 * result + ( nodeState != null ? nodeState.hashCode() : 0 );
        result = 31 * result + ( recursive ? 1 : 0 );
        return result;
    }
}
