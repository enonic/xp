package com.enonic.xp.node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

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

    public static NodeCommitEntries from( final Collection<NodeCommitEntry> nodeCommitEntries )
    {
        return fromInternal( ImmutableSet.copyOf( nodeCommitEntries ) );
    }

    private static NodeCommitEntries fromInternal( final ImmutableSet<NodeCommitEntry> set )
    {
        return set.isEmpty() ? EMPTY : new NodeCommitEntries( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final ImmutableSet.Builder<NodeCommitEntry> nodeCommitEntries = ImmutableSet.builder();

        public Builder add( final NodeCommitEntry nodeCommitEntry )
        {
            this.nodeCommitEntries.add( nodeCommitEntry );
            return this;
        }

        public Builder addAll( final Collection<NodeCommitEntry> nodeCommitEntries )
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
