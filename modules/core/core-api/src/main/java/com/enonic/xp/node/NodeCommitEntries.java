package com.enonic.xp.node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class NodeCommitEntries
    extends AbstractImmutableEntitySet<NodeCommitEntry>
{
    private NodeCommitEntries( final ImmutableSet<NodeCommitEntry> set )
    {
        super( set );
    }

    public static NodeCommitEntries empty()
    {
        return new NodeCommitEntries( ImmutableSet.of() );
    }

    public static NodeCommitEntries from( final NodeCommitEntry... nodeCommitEntries )
    {
        return new NodeCommitEntries( ImmutableSet.copyOf( nodeCommitEntries ) );
    }


    public static NodeCommitEntries from( final Collection<NodeCommitEntry> nodeCommitEntries )
    {
        return new NodeCommitEntries( ImmutableSet.copyOf( nodeCommitEntries ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final Set<NodeCommitEntry> nodeCommitEntries = new LinkedHashSet<>();

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
            return new NodeCommitEntries( ImmutableSet.copyOf( this.nodeCommitEntries ) );
        }

    }

}
