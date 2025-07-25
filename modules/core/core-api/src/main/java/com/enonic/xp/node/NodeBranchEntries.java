package com.enonic.xp.node;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class NodeBranchEntries
    implements Iterable<NodeBranchEntry>
{
    private static final NodeBranchEntries EMPTY = new NodeBranchEntries( Collections.emptySet() );

    private final Map<NodeId, NodeBranchEntry> branchNodeVersionMap;

    private NodeBranchEntries( final Builder builder )
    {
        this.branchNodeVersionMap = Collections.unmodifiableMap( builder.map );
    }

    private NodeBranchEntries( final Iterable<? extends NodeBranchEntry> entries )
    {
        Map<NodeId, NodeBranchEntry> builder = new LinkedHashMap<>();
        for ( NodeBranchEntry entry : entries )
        {
            builder.put( entry.getNodeId(), entry );
        }
        this.branchNodeVersionMap = Collections.unmodifiableMap( builder );
    }

    public static NodeBranchEntries from( final Iterable<? extends NodeBranchEntry> nodeBranchEntries )
    {
        return new NodeBranchEntries( nodeBranchEntries );
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
        private final Map<NodeId, NodeBranchEntry> map = new LinkedHashMap<>();

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
            return this.map.isEmpty() ? EMPTY : new NodeBranchEntries( this );
        }
    }
}
