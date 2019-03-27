package com.enonic.xp.node;

import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public class NodeCommitIds
    extends AbstractImmutableEntitySet<NodeCommitId>
{
    private NodeCommitIds( final ImmutableSet<NodeCommitId> set )
    {
        super( set );
    }

    public static NodeCommitIds empty()
    {
        return new NodeCommitIds( ImmutableSet.<NodeCommitId>of() );
    }

    public static NodeCommitIds from( final NodeCommitId... nodeCommitIds )
    {
        return new NodeCommitIds( ImmutableSet.copyOf( nodeCommitIds ) );
    }


    public static NodeCommitIds from( final Collection<NodeCommitId> nodeCommitIds )
    {
        return new NodeCommitIds( ImmutableSet.copyOf( nodeCommitIds ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final Set<NodeCommitId> nodeCommitIds = Sets.newLinkedHashSet();

        public Builder add( final NodeCommitId nodeCommitId )
        {
            this.nodeCommitIds.add( nodeCommitId );
            return this;
        }

        public Builder addAll( final Collection<NodeCommitId> nodeCommitIds )
        {
            this.nodeCommitIds.addAll( nodeCommitIds );
            return this;
        }

        public NodeCommitIds build()
        {
            return new NodeCommitIds( ImmutableSet.copyOf( this.nodeCommitIds ) );
        }

    }

}
