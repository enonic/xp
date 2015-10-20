package com.enonic.xp.repo.impl.branch.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.support.AbstractImmutableEntityList;

public class NodesBranchMetadata
    extends AbstractImmutableEntityList<NodeBranchMetadata>
{
    private final Map<NodeId, NodeBranchMetadata> branchNodeVersionMap = Maps.newHashMap();

    private NodesBranchMetadata( final Builder builder )
    {
        super( ImmutableList.copyOf( builder.branchNodeVersions ) );

        for ( final NodeBranchMetadata nodeBranchMetadata : list )
        {
            branchNodeVersionMap.put( nodeBranchMetadata.getNodeId(), nodeBranchMetadata );
        }
    }

    public Set<NodeId> getKeys()
    {
        return branchNodeVersionMap.keySet();
    }

    private NodesBranchMetadata( final ImmutableList<NodeBranchMetadata> list )
    {
        super( list );

        for ( final NodeBranchMetadata nodeBranchMetadata : list )
        {
            branchNodeVersionMap.put( nodeBranchMetadata.getNodeId(), nodeBranchMetadata );
        }
    }

    public NodeBranchMetadata get( final NodeId nodeId )
    {
        return branchNodeVersionMap.get( nodeId );
    }

    public static NodesBranchMetadata from( final Collection<NodeBranchMetadata> nodeBranchMetadatas )
    {
        return new NodesBranchMetadata( ImmutableList.copyOf( nodeBranchMetadatas ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<NodeBranchMetadata> branchNodeVersions = Lists.newLinkedList();

        public Builder add( final NodeBranchMetadata nodeBranchMetadata )
        {
            this.branchNodeVersions.add( nodeBranchMetadata );
            return this;
        }

        public NodesBranchMetadata build()
        {
            return new NodesBranchMetadata( this );
        }

    }


}