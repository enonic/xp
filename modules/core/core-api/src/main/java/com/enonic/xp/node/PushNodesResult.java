package com.enonic.xp.node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Beta
public class PushNodesResult
{
    private final ImmutableMap<NodeId, PushNodeEntry> successful;

    private final ImmutableSet<Failed> failed;

    private PushNodesResult( Builder builder )
    {
        successful = ImmutableMap.copyOf( builder.successful );
        failed = ImmutableSet.copyOf( builder.failed );
    }

    public ImmutableMap<NodeId, PushNodeEntry> getSuccessful()
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
        private final LinkedHashMap<NodeId, PushNodeEntry> successful = Maps.newLinkedHashMap();

        private final List<Failed> failed = Lists.newLinkedList();

        private final Set<NodePath> addedParentPaths = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder addSuccess( final PushNodeEntry success )
        {
            this.successful.put( success.getNodeBranchEntry().getNodeId(), success );
            this.addedParentPaths.add( success.getNodeBranchEntry().getNodePath() );
            return this;
        }

        public Builder addFailed( final NodeBranchEntry failed, final Reason reason )
        {
            this.failed.add( new Failed( failed, reason ) );
            return this;
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
