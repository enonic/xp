package com.enonic.xp.node;

import java.util.ArrayList;
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

    private NodeIds( final ImmutableSet<NodeId> set )
    {
        super( set );
    }

    private NodeIds( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.nodeIds ) );
    }

    public static NodeIds empty()
    {
        final ImmutableSet<NodeId> set = ImmutableSet.of();
        return new NodeIds( set );
    }

    public static NodeIds from( final NodeId... ids )
    {
        return new NodeIds( ImmutableSet.copyOf( ids ) );
    }

    public static NodeIds from( final String... ids )
    {
        return new NodeIds( parseIds( Arrays.asList( ids ) ) );
    }

    public static NodeIds from( final Iterable<NodeId> ids )
    {
        return new NodeIds( ImmutableSet.copyOf( ids ) );
    }

    public static NodeIds from( final Collection<String> ids )
    {
        return new NodeIds( parseIds( ids ) );
    }


    public static Builder create()
    {
        return new Builder();
    }

    private static ImmutableSet<NodeId> parseIds( final Collection<String> paths )
    {
        return paths.stream().map( NodeId::from ).collect( ImmutableSet.toImmutableSet() );
    }

    public List<String> getAsStrings()
    {
        return this.set.stream().map( NodeId::toString ).collect( Collectors.toList() );
    }

    public static class Builder
    {
        private final List<NodeId> nodeIds = new ArrayList<>();

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
            return new NodeIds( this );
        }

    }

}
