package com.enonic.xp.repo.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.node.NodeId;

public final class NodeBranchEntries
    implements Iterable<NodeBranchEntry>
{
    private static final NodeBranchEntries EMPTY = new NodeBranchEntries( ImmutableMap.of() );

    /** {@code null} exactly while this instance is an un-materialized lazy listing — see {@link #lazy}. */
    private ImmutableMap<NodeId, NodeBranchEntry> branchNodeVersionMap;

    private final int lazySize;

    private final Iterable<NodeBranchEntry> lazySource;

    private NodeBranchEntries( final ImmutableMap<NodeId, NodeBranchEntry> entries )
    {
        this.branchNodeVersionMap = entries;
        this.lazySize = 0;
        this.lazySource = null;
    }

    private NodeBranchEntries( final int size, final Iterable<NodeBranchEntry> source )
    {
        this.branchNodeVersionMap = null;
        this.lazySize = size;
        this.lazySource = source;
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

    /**
     * A listing whose entries are produced on demand (Phase 4 decision D2,
     * nodb/BUILD-PHASE-4.md): {@code size} is known up front — the storage surface returns an
     * indexed count alongside the first page — while the entries stream in keyset-paged batches
     * as {@link #iterator()} walks them.
     * <p>
     * This exists because a whole-branch listing is unbounded: {@code ReindexExecutor} reports
     * {@code getSize()} to its listener and then consumes ONE ENTRY AT A TIME, so nothing above
     * this class had to change for reindex to stop materializing a branch. Every other accessor
     * ({@link #getSet()}, {@link #stream()}, {@link #getKeys()}, {@link #get}) needs the whole
     * map and therefore materializes on first use — which is exactly what the two delete paths
     * want (one bounded by a subtree, one an admin whole-branch operation that hands the full set
     * to a batched delete).
     */
    public static NodeBranchEntries lazy( final int size, final Iterable<NodeBranchEntry> entries )
    {
        return new NodeBranchEntries( size, entries );
    }

    public int getSize()
    {
        return this.branchNodeVersionMap == null ? this.lazySize : this.branchNodeVersionMap.size();
    }

    public boolean isNotEmpty()
    {
        return getSize() > 0;
    }

    public Collection<NodeBranchEntry> getSet()
    {
        return materialized().values();
    }

    public Stream<NodeBranchEntry> stream()
    {
        return materialized().values().stream();
    }

    @Override
    public Iterator<NodeBranchEntry> iterator()
    {
        return this.branchNodeVersionMap == null ? this.lazySource.iterator() : this.branchNodeVersionMap.values().iterator();
    }

    public Set<NodeId> getKeys()
    {
        return materialized().keySet();
    }

    public NodeBranchEntry get( final NodeId nodeId )
    {
        return materialized().get( nodeId );
    }

    private ImmutableMap<NodeId, NodeBranchEntry> materialized()
    {
        if ( this.branchNodeVersionMap == null )
        {
            final ImmutableMap.Builder<NodeId, NodeBranchEntry> map = ImmutableMap.builder();
            this.lazySource.forEach( entry -> map.put( entry.getNodeId(), entry ) );
            this.branchNodeVersionMap = map.buildKeepingLast();
        }
        return this.branchNodeVersionMap;
    }

    public static final class Builder
    {
        private final ImmutableMap.Builder<NodeId, NodeBranchEntry> map = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder add( final NodeBranchEntry nodeBranchEntry )
        {
            this.map.put( nodeBranchEntry.getNodeId(), nodeBranchEntry );
            return this;
        }

        public Builder addAll( final NodeBranchEntries nodeBranchEntries )
        {
            // Iterated rather than map-copied: a lazy listing has no map until it is walked.
            nodeBranchEntries.forEach( this::add );
            return this;
        }

        public NodeBranchEntries build()
        {
            return fromInternal( map.buildKeepingLast() );
        }
    }
}
