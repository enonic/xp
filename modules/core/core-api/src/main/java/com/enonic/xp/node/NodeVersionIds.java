package com.enonic.xp.node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class NodeVersionIds
    extends AbstractImmutableEntitySet<NodeVersionId>
{
    private NodeVersionIds( final ImmutableSet<NodeVersionId> set )
    {
        super( set );
    }

    public static NodeVersionIds empty()
    {
        return new NodeVersionIds( ImmutableSet.of() );
    }

    public static NodeVersionIds from( final NodeVersionId... nodeVersionIds )
    {
        return new NodeVersionIds( ImmutableSet.copyOf( nodeVersionIds ) );
    }


    public static NodeVersionIds from( final Collection<NodeVersionId> nodeVersionIds )
    {
        return new NodeVersionIds( ImmutableSet.copyOf( nodeVersionIds ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        final Set<NodeVersionId> nodeVersionIds = new LinkedHashSet<>();

        public Builder add( final NodeVersionId nodeVersionId )
        {
            this.nodeVersionIds.add( nodeVersionId );
            return this;
        }

        public Builder addAll( final Collection<NodeVersionId> nodeVersionIds )
        {
            this.nodeVersionIds.addAll( nodeVersionIds );
            return this;
        }


        public NodeVersionIds build()
        {
            return new NodeVersionIds( ImmutableSet.copyOf( this.nodeVersionIds ) );
        }

    }

}
