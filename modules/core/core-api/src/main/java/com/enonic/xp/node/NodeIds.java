package com.enonic.xp.node;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class NodeIds
    extends AbstractImmutableEntitySet<NodeId>
{
    private static final NodeIds EMPTY = new NodeIds( ImmutableSet.of() );

    private NodeIds( final ImmutableSet<NodeId> set )
    {
        super( set );
    }

    public static NodeIds empty()
    {
        return EMPTY;
    }

    public static NodeIds from( final NodeId... ids )
    {
        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static NodeIds from( final String... ids )
    {
        return from( Arrays.asList( ids ) );
    }

    public static NodeIds from( final Collection<String> ids )
    {
        return fromInternal( ids.stream().map( NodeId::from ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static NodeIds from( final Iterable<NodeId> ids )
    {
        if ( ids instanceof NodeIds )
        {
            return (NodeIds) ids;
        }
        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    private static NodeIds fromInternal( final ImmutableSet<NodeId> set )
    {
        if ( set.isEmpty() )
        {
            return EMPTY;
        }
        else
        {
            return new NodeIds( set );
        }
    }

    public List<String> getAsStrings()
    {
        return this.set.stream().map( NodeId::toString ).collect( Collectors.toList() );
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<NodeId> nodeIds = ImmutableSet.builder();

        public Builder add( final NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public Builder addAll( final NodeIds nodeIds )
        {
            this.nodeIds.addAll( nodeIds.getSet() );
            return this;
        }

        public NodeIds build()
        {
            return fromInternal( nodeIds.build() );
        }
    }
}
