package com.enonic.xp.node;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.CompareStatus;

@Beta
public class NodeComparison
{
    private final NodePath sourcePath;

    private final NodePath targetPath;

    private final NodeId sourceId;

    private final NodeId targetId;

    private final boolean targetInherited;

    private final CompareStatus compareStatus;

    public NodeComparison( final NodeBranchEntry sourceEntry, final NodeBranchEntry targetEntry, final CompareStatus compareStatus )
    {
        this.sourceId = sourceEntry != null ? sourceEntry.getNodeId() : null;
        this.targetId = targetEntry != null ? targetEntry.getNodeId() : null;
        this.sourcePath = sourceEntry != null ? sourceEntry.getNodePath() : null;
        this.targetPath = targetEntry != null ? targetEntry.getNodePath() : null;
        this.targetInherited = targetEntry != null && targetEntry.isInherited();

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

    public boolean isTargetInherited()
    {
        return targetInherited;
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
        return targetInherited == that.targetInherited && Objects.equals( sourcePath, that.sourcePath ) &&
            Objects.equals( targetPath, that.targetPath ) && Objects.equals( sourceId, that.sourceId ) &&
            Objects.equals( targetId, that.targetId ) && compareStatus == that.compareStatus;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( sourcePath, targetPath, sourceId, targetId, targetInherited, compareStatus );
    }
}
