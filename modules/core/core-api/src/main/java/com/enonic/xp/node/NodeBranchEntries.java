package com.enonic.xp.node;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class NodeBranchEntries
    implements Iterable<NodeBranchEntry>
{
    private final Map<NodeId, NodeBranchEntry> branchNodeVersionMap;

    private NodeBranchEntries( final Builder builder )
    {
        this.branchNodeVersionMap = builder.map;
    }

    private NodeBranchEntries( final Collection<NodeBranchEntry> entries )
    {
        branchNodeVersionMap = new LinkedHashMap<>();
        entries.stream().forEach( entry -> branchNodeVersionMap.put( entry.getNodeId(), entry ) );
    }

    public static NodeBranchEntries from( final Collection<NodeBranchEntry> nodeBranchEntries )
    {
        return new NodeBranchEntries( nodeBranchEntries );
    }

    public static NodeBranchEntries empty()
    {
        return NodeBranchEntries.create().build();
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

    public static class Builder
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
            return new NodeBranchEntries( this );
        }

    }


}