package com.enonic.xp.node;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

public final class NodeBranchEntries
    implements Iterable<NodeBranchEntry>
{
    private static final NodeBranchEntries EMPTY = new NodeBranchEntries( ImmutableMap.of() );

    private final ImmutableMap<NodeId, NodeBranchEntry> branchNodeVersionMap;

    private NodeBranchEntries( final ImmutableMap<NodeId, NodeBranchEntry> entries )
    {
        this.branchNodeVersionMap = entries;
    }

    private static NodeBranchEntries fromInternal( final ImmutableMap<NodeId, NodeBranchEntry> entries )
    {
        return entries.isEmpty() ? EMPTY : new NodeBranchEntries( entries );
    }

    public static NodeBranchEntries empty()
    {
        return EMPTY;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public int getSize()
    {
        return this.branchNodeVersionMap.size();
    }

    public boolean isNotEmpty()
    {
        return !this.branchNodeVersionMap.isEmpty();
    }

    public Collection<NodeBranchEntry> getSet()
    {
        return this.branchNodeVersionMap.values();
    }

    public Stream<NodeBranchEntry> stream()
    {
        return this.branchNodeVersionMap.values().stream();
    }

    @Override
    public Iterator<NodeBranchEntry> iterator()
    {
        return this.branchNodeVersionMap.values().iterator();
    }

    public Set<NodeId> getKeys()
    {
        return branchNodeVersionMap.keySet();
    }

    public NodeBranchEntry get( final NodeId nodeId )
    {
        return branchNodeVersionMap.get( nodeId );
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<NodeId, NodeBranchEntry> map = ImmutableMap.builder();

        public Builder add( final NodeBranchEntry nodeBranchEntry )
        {
            this.map.put( nodeBranchEntry.getNodeId(), nodeBranchEntry );
            return this;
        }

        public Builder addAll( final NodeBranchEntries nodeBranchEntries )
        {
            this.map.putAll( nodeBranchEntries.branchNodeVersionMap );
            return this;
        }

        public NodeBranchEntries build()
        {
            return fromInternal( map.buildKeepingLast() );
        }
    }
}
