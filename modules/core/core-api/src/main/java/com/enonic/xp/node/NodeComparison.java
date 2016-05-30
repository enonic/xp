package com.enonic.xp.node;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.CompareStatus;

@Beta
public class NodeComparison
{
    private final NodeBranchEntry sourceEntry;

    private final NodeBranchEntry targetEntry;

    private final CompareStatus compareStatus;

    public NodeComparison( final NodeBranchEntry sourceEntry, final NodeBranchEntry targetEntry, final CompareStatus compareStatus )
    {
        this.sourceEntry = sourceEntry;
        this.targetEntry = targetEntry;
        this.compareStatus = compareStatus;
    }


    public NodeId getNodeId()
    {
        return sourceEntry != null ? sourceEntry.getNodeId() : targetEntry != null ? targetEntry.getNodeId() : null;
    }

    public NodeBranchEntry getSourceEntry()
    {
        return sourceEntry;
    }

    public NodeBranchEntry getTargetEntry()
    {
        return targetEntry;
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
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final NodeComparison that = (NodeComparison) o;

        if ( sourceEntry != null ? !sourceEntry.equals( that.sourceEntry ) : that.sourceEntry != null )
        {
            return false;
        }
        return compareStatus == that.compareStatus;

    }

    @Override
    public int hashCode()
    {
        int result = sourceEntry != null ? sourceEntry.hashCode() : 0;
        result = 31 * result + ( compareStatus != null ? compareStatus.hashCode() : 0 );
        return result;
    }
}
