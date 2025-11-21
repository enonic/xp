package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.CompareStatus;

@PublicApi
public final class NodeComparison
{
    private final NodePath sourcePath;

    private final NodePath targetPath;

    private final NodeId sourceId;

    private final NodeId targetId;

    private final NodeCompareStatus compareStatus;

    public NodeComparison( final NodeId sourceId, final NodePath sourcePath, NodeId targetId, NodePath targetPath, final NodeCompareStatus compareStatus )
    {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;

        this.compareStatus = compareStatus;
    }

    public NodeId getNodeId()
    {
        return sourceId != null ? sourceId : targetId;
    }

    public NodeCompareStatus getCompareStatus()
    {
        return compareStatus;
    }

    public NodePath getSourcePath()
    {
        return sourcePath;
    }

    public NodePath getTargetPath()
    {
        return targetPath;
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

        final NodeComparison that = (NodeComparison) o;

        if ( sourcePath != null ? !sourcePath.equals( that.sourcePath ) : that.sourcePath != null )
        {
            return false;
        }
        if ( targetPath != null ? !targetPath.equals( that.targetPath ) : that.targetPath != null )
        {
            return false;
        }
        if ( sourceId != null ? !sourceId.equals( that.sourceId ) : that.sourceId != null )
        {
            return false;
        }
        if ( targetId != null ? !targetId.equals( that.targetId ) : that.targetId != null )
        {
            return false;
        }
        return compareStatus == that.compareStatus;

    }

    @Override
    public int hashCode()
    {
        int result = sourcePath != null ? sourcePath.hashCode() : 0;
        result = 31 * result + ( targetPath != null ? targetPath.hashCode() : 0 );
        result = 31 * result + ( sourceId != null ? sourceId.hashCode() : 0 );
        result = 31 * result + ( targetId != null ? targetId.hashCode() : 0 );
        result = 31 * result + ( compareStatus != null ? compareStatus.hashCode() : 0 );
        return result;
    }
}
