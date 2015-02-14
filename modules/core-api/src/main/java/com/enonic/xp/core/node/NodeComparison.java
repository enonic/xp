package com.enonic.xp.core.node;

import com.enonic.xp.core.content.CompareStatus;

public class NodeComparison
{
    private final NodeId nodeId;

    private final CompareStatus compareStatus;

    public NodeComparison( final NodeId nodeId, final CompareStatus compareStatus )
    {
        this.nodeId = nodeId;
        this.compareStatus = compareStatus;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public CompareStatus getCompareStatus()
    {
        return compareStatus;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof NodeComparison ) )
        {
            return false;
        }

        final NodeComparison that = (NodeComparison) o;

        if ( compareStatus != null ? !compareStatus.equals( that.compareStatus ) : that.compareStatus != null )
        {
            return false;
        }
        if ( nodeId != null ? !nodeId.equals( that.nodeId ) : that.nodeId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = nodeId != null ? nodeId.hashCode() : 0;
        result = 31 * result + ( compareStatus != null ? compareStatus.hashCode() : 0 );
        return result;
    }
}
