package com.enonic.xp.node;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Nodes
    extends AbstractImmutableEntitySet<Node>
{
    private static final Nodes EMPTY = new Nodes( ImmutableSet.of() );

    private Nodes( final ImmutableSet<Node> set )
    {
        super( set );
    }

    public static Nodes empty()
    {
        return EMPTY;
    }

    public static Nodes from( final Node... nodes )
    {
        return fromInternal( ImmutableSet.copyOf( nodes ) );
    }

    public static Nodes from( final Iterable<? extends Node> nodes )
    {
        return nodes instanceof Nodes n ? n : fromInternal( ImmutableSet.copyOf( nodes ) );
    }

    public static Collector<Node, ?, Nodes> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), Nodes::fromInternal );
    }

    private static Nodes fromInternal( final ImmutableSet<Node> set )
    {
        return set.isEmpty() ? EMPTY : new Nodes( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodePaths getPaths()
    {
        return set.stream().map( Node::path ).collect( NodePaths.collector() );
    }

    public NodeIds getIds()
    {
        return set.stream().map( Node::id ).collect( NodeIds.collector() );
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<Node> nodes = new ImmutableSet.Builder<>();

        public Builder add( Node node )
        {
            nodes.add( node );
            return this;
        }

        public Builder addAll( Iterable<? extends Node> nodes )
        {
            this.nodes.addAll( nodes );
            return this;
        }

        public Nodes build()
        {
            return new Nodes( nodes.build() );
        }
    }
}
