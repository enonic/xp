package com.enonic.xp.node;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class PushNodesResult
{
    private final NodeBranchEntries successful;

    private final ImmutableSet<Failed> failed;

    protected PushNodesResult( Builder<?> builder )
    {
        successful = builder.successful.build();
        failed = builder.failed.build();
    }

    public NodeBranchEntries getSuccessful()
    {
        return successful;
    }

    public ImmutableSet<Failed> getFailed()
    {
        return failed;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder<T extends Builder>
    {
        private final NodeBranchEntries.Builder successful = NodeBranchEntries.create();

        private final ImmutableSet.Builder<Failed> failed = ImmutableSet.builder();

        private final Set<NodePath> addedParentPaths = new HashSet<>();

        protected Builder()
        {
        }

        public T addSuccess( final NodeBranchEntry success )
        {
            this.successful.add( success );
            this.addedParentPaths.add( success.getNodePath() );
            return (T) this;
        }

        public T addFailed( final NodeBranchEntry failed, final Reason reason )
        {
            this.failed.add( new Failed( failed, reason ) );
            return (T) this;
        }

        public boolean hasBeenAdded( final NodePath parentPath )
        {
            return this.addedParentPaths.contains( parentPath );
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
