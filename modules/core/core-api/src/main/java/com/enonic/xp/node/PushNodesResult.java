package com.enonic.xp.node;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class PushNodesResult
{
    private final ImmutableList<PushNodeEntry> successful;

    private final ImmutableList<Failed> failed;

    protected PushNodesResult( Builder<?> builder )
    {
        successful = builder.successful.build();
        failed = builder.failed.build();
    }

    public NodeBranchEntries getSuccessful()
    {
        final NodeBranchEntries.Builder builder = NodeBranchEntries.create();
        successful.stream().map( PushNodeEntry::getNodeBranchEntry ).forEach( builder::add );
        return builder.build();
    }

    public List<PushNodeEntry> getSuccessfulEntries()
    {
        return successful;
    }

    public List<Failed> getFailedEntries()
    {
        return failed;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder<T extends Builder>
    {
        private final ImmutableList.Builder<PushNodeEntry> successful = ImmutableList.builder();

        private final ImmutableList.Builder<Failed> failed = ImmutableList.builder();

        protected Builder()
        {
        }

        public T addSuccess( final NodeBranchEntry nodeBranchEntry, NodePath currentTargetPath )
        {
            successful.add( PushNodeEntry.create().nodeBranchEntry( nodeBranchEntry ).currentTargetPath( currentTargetPath ).build() );
            return (T) this;
        }

        @Deprecated
        public T addSuccess( final NodeBranchEntry success )
        {
            return (T) this;
        }

        public T addFailed( final NodeBranchEntry failed, final Reason reason )
        {
            this.failed.add( new Failed( failed, reason ) );
            return (T) this;
        }

        @Deprecated
        public boolean hasBeenAdded( final NodePath parentPath )
        {
            return false;
        }

        public PushNodesResult build()
        {
            return new PushNodesResult( this );
        }
    }

    public enum Reason
    {
        ALREADY_EXIST,
        PARENT_NOT_FOUND,
        ACCESS_DENIED
    }

    public static final class Failed
    {
        private final NodeBranchEntry nodeBranchEntry;

        private final Reason reason;

        public Failed( final NodeBranchEntry nodeBranchEntry, final Reason reason )
        {
            this.nodeBranchEntry = nodeBranchEntry;
            this.reason = reason;
        }

        public NodeBranchEntry getNodeBranchEntry()
        {
            return nodeBranchEntry;
        }

        public Reason getReason()
        {
            return reason;
        }
    }
}
