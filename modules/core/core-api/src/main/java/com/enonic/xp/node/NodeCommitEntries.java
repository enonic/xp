package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class NodeCommitEntries
    extends AbstractImmutableEntitySet<NodeCommitEntry>
{
    private static final NodeCommitEntries EMPTY = new NodeCommitEntries( ImmutableSet.of() );

    private NodeCommitEntries( final ImmutableSet<NodeCommitEntry> set )
    {
        super( set );
    }

    public static NodeCommitEntries empty()
    {
        return EMPTY;
    }

    public static NodeCommitEntries from( final NodeCommitEntry... nodeCommitEntries )
    {
        return fromInternal( ImmutableSet.copyOf( nodeCommitEntries ) );
    }

    public static NodeCommitEntries from( final Iterable<NodeCommitEntry> nodeCommitEntries )
    {
        return nodeCommitEntries instanceof NodeCommitEntries n ? n : fromInternal( ImmutableSet.copyOf( nodeCommitEntries ) );
    }

    public static Collector<NodeCommitEntry, ?, NodeCommitEntries> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), NodeCommitEntries::fromInternal );
    }

    private static NodeCommitEntries fromInternal( final ImmutableSet<NodeCommitEntry> set )
    {
        return set.isEmpty() ? EMPTY : new NodeCommitEntries( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final ImmutableSet.Builder<NodeCommitEntry> nodeCommitEntries = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder add( final NodeCommitEntry nodeCommitEntry )
        {
            this.nodeCommitEntries.add( nodeCommitEntry );
            return this;
        }

        public Builder addAll( final Iterable<NodeCommitEntry> nodeCommitEntries )
        {
            this.nodeCommitEntries.addAll( nodeCommitEntries );
            return this;
        }

        public NodeCommitEntries build()
        {
            return fromInternal( this.nodeCommitEntries.build() );
        }
    }
}
