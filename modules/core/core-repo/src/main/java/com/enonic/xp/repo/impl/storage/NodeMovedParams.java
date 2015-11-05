package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class NodeMovedParams
{
    private final NodePath existingPath;

    private final NodePath newPath;

    private final NodeId nodeId;

    public NodeMovedParams( final NodePath existingPath, final NodePath newPath, final NodeId nodeId )
    {
        this.existingPath = existingPath;
        this.newPath = newPath;
        this.nodeId = nodeId;
    }

    public NodePath getExistingPath()
    {
        return existingPath;
    }

    public NodePath getNewPath()
    {
        return newPath;
    }

    public NodeId getNodeId()
    {
        return nodeId;
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

        final NodeMovedParams that = (NodeMovedParams) o;

        if ( existingPath != null ? !existingPath.equals( that.existingPath ) : that.existingPath != null )
        {
            return false;
        }
        if ( newPath != null ? !newPath.equals( that.newPath ) : that.newPath != null )
        {
            return false;
        }
        return !( nodeId != null ? !nodeId.equals( that.nodeId ) : that.nodeId != null );

    }

    @Override
    public int hashCode()
    {
        int result = existingPath != null ? existingPath.hashCode() : 0;
        result = 31 * result + ( newPath != null ? newPath.hashCode() : 0 );
        result = 31 * result + ( nodeId != null ? nodeId.hashCode() : 0 );
        return result;
    }
}


