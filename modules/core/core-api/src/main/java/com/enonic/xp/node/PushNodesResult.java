package com.enonic.xp.node;

import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

@Beta
public class PushNodesResult
{
    private final NodeBranchEntries successful;

    private final ImmutableSet<Failed> failed;

    private PushNodesResult( Builder builder )
    {
        successful = NodeBranchEntries.from( builder.successful );
        failed = ImmutableSet.copyOf( builder.failed );
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

    public static final class Builder
    {
        private final List<NodeBranchEntry> successful = Lists.newLinkedList();

        private final List<Failed> failed = Lists.newLinkedList();

        private Builder()
        {
        }

        public Builder addSuccess( final NodeBranchEntry success )
        {
            this.successful.add( success );
            return this;
        }

        public Builder addFailed( final NodeBranchEntry failed, final Reason reason )
        {
            this.failed.add( new Failed( failed, reason ) );
            return this;
        }

        public PushNodesResult build()
        {
            return new PushNodesResult( this );
        }
    }

    public enum Reason
    {
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
