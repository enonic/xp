package com.enonic.xp.blob;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class NodeVersionKeys
    extends AbstractImmutableEntitySet<NodeVersionKey>
{
    private NodeVersionKeys( final ImmutableSet<NodeVersionKey> set )
    {
        super( set );
    }

    public static NodeVersionKeys empty()
    {
        return new NodeVersionKeys( ImmutableSet.of() );
    }

    public static NodeVersionKeys from( final NodeVersionKey... keys )
    {
        return new NodeVersionKeys( ImmutableSet.copyOf( keys ) );
    }


    public static NodeVersionKeys from( final Collection<NodeVersionKey> keys )
    {
        return new NodeVersionKeys( ImmutableSet.copyOf( keys ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        final ImmutableSet.Builder<NodeVersionKey> keys = ImmutableSet.builder();

        public Builder add( final NodeVersionKey key )
        {
            this.keys.add( key );
            return this;
        }

        public Builder addAll( final Iterable<? extends NodeVersionKey> key )
        {
            this.keys.addAll( key );
            return this;
        }

        public NodeVersionKeys build()
        {
            return new NodeVersionKeys( this.keys.build() );
        }
    }

}
