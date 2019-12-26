package com.enonic.xp.node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class NodeCommitIds
    extends AbstractImmutableEntitySet<NodeCommitId>
{
    private NodeCommitIds( final ImmutableSet<NodeCommitId> set )
    {
        super( set );
    }

    public static NodeCommitIds empty()
    {
        return new NodeCommitIds( ImmutableSet.of() );
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
        final Set<NodeCommitId> nodeCommitIds = new LinkedHashSet<>();

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
