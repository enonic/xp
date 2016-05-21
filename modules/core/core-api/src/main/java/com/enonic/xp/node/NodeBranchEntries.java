package com.enonic.xp.node;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class NodeBranchEntries
    extends AbstractImmutableEntitySet<NodeBranchEntry>
{
    private final Map<NodeId, NodeBranchEntry> branchNodeVersionMap = Maps.newHashMap();

    private NodeBranchEntries( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.branchNodeVersions ) );

        for ( final NodeBranchEntry nodeBranchEntry : set )
        {
            branchNodeVersionMap.put( nodeBranchEntry.getNodeId(), nodeBranchEntry );
        }
    }

    private NodeBranchEntries( final ImmutableSet<NodeBranchEntry> list )
    {
        super( list );

        for ( final NodeBranchEntry nodeBranchEntry : list )
        {
            branchNodeVersionMap.put( nodeBranchEntry.getNodeId(), nodeBranchEntry );
        }
    }

    public static NodeBranchEntries from( final Collection<NodeBranchEntry> nodeBranchEntries )
    {
        return new NodeBranchEntries( ImmutableSet.copyOf( nodeBranchEntries ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<NodeId> getKeys()
    {
        return branchNodeVersionMap.keySet();
    }

    public NodeBranchEntry get( final NodeId nodeId )
    {
        return branchNodeVersionMap.get( nodeId );
    }

    public static class Builder
    {
        private final List<NodeBranchEntry> branchNodeVersions = Lists.newLinkedList();

        public Builder add( final NodeBranchEntry nodeBranchEntry )
        {
            this.branchNodeVersions.add( nodeBranchEntry );
            return this;
        }

        public NodeBranchEntries build()
        {
            return new NodeBranchEntries( this );
        }

    }


}