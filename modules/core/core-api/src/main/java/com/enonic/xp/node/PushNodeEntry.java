package com.enonic.xp.node;

public class PushNodeEntry
{
    private NodeBranchEntry nodeBranchEntry;

    private NodePath currentTargetPath;

    private PushNodeEntry( final Builder builder )
    {
        nodeBranchEntry = builder.nodeBranchEntry;
        currentTargetPath = builder.currentTargetPath;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeBranchEntry getNodeBranchEntry()
    {
        return nodeBranchEntry;
    }

    public NodePath getCurrentTargetPath()
    {
        return currentTargetPath;
    }

    @Override
    public int hashCode()
    {
        int result = nodeBranchEntry != null ? nodeBranchEntry.hashCode() : 0;
        result = 31 * result + ( nodeVersionId != null ? nodeVersionId.hashCode() : 0 );
        result = 31 * result + ( previousPath != null ? previousPath.hashCode() : 0 );
        return result;
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

        final PushNodeEntry that = (PushNodeEntry) o;

        if ( nodeBranchEntry != null ? !nodeBranchEntry.equals( that.nodeBranchEntry ) : that.nodeBranchEntry != null )
        {
            return false;
        }
        if ( nodeVersionId != null ? !nodeVersionId.equals( that.nodeVersionId ) : that.nodeVersionId != null )
        {
            return false;
        }
        if ( previousPath != null ? !previousPath.equals( that.previousPath ) : that.previousPath != null )
        {
            return false;
        }
        return true;
    }

    public static final class Builder
    {
        private NodeBranchEntry nodeBranchEntry;

        private NodePath currentTargetPath;

        private Builder()
        {
        }

        public Builder nodeBranchEntry( final NodeBranchEntry val )
        {
            nodeBranchEntry = val;
            return this;
        }

        public Builder currentTargetPath( final NodePath val )
        {
            currentTargetPath = val;
            return this;
        }

        public PushNodeEntry build()
        {
            return new PushNodeEntry( this );
        }
    }
}
