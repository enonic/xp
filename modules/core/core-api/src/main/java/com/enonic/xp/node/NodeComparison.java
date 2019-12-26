package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.CompareStatus;

@PublicApi
public class NodeComparison
{
    private final NodePath sourcePath;

    private final NodePath targetPath;

    private final NodeId sourceId;

    private final NodeId targetId;

    private final CompareStatus compareStatus;

    public NodeComparison( final NodeBranchEntry sourceEntry, final NodeBranchEntry targetEntry, final CompareStatus compareStatus )
    {
        this.sourceId = sourceEntry != null ? sourceEntry.getNodeId() : null;
        this.targetId = targetEntry != null ? targetEntry.getNodeId() : null;
        this.sourcePath = sourceEntry != null ? sourceEntry.getNodePath() : null;
        this.targetPath = targetEntry != null ? targetEntry.getNodePath() : null;

        this.compareStatus = compareStatus;
    }

    public NodeId getNodeId()
    {
        return sourceId != null ? sourceId : targetId;
    }

    public CompareStatus getCompareStatus()
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
