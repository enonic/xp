package com.enonic.xp.node;

import java.util.Collection;
import java.util.stream.Collector;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Nodes
    extends AbstractImmutableEntitySet<Node>
{
    private Nodes( final ImmutableSet<Node> set )
    {
        super( set );
    }

    public static Nodes empty()
    {
        return new Nodes( ImmutableSet.of() );
    }

    public static Nodes from( final Node... nodes )
    {
        return new Nodes( ImmutableSet.copyOf( nodes ) );
    }

    public static Nodes from( final Iterable<? extends Node> nodes )
    {
        return new Nodes( ImmutableSet.copyOf( nodes ) );
    }

    public static Nodes from( final Collection<? extends Node> nodes )
    {
        return new Nodes( ImmutableSet.copyOf( nodes ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Collector<Node, ?, Nodes> collecting()
    {
        return Collector.of( Builder::new, Builder::add, ( left, right ) -> left.addAll( right.build() ), Builder::build );
    }

    @Deprecated
    public Node getNodeById( final NodeId nodeId )
    {
        return this.set.stream().filter( n -> nodeId.equals( n.id() ) ).findAny().orElse( null );
    }

    public NodePaths getPaths()
    {
        return NodePaths.from( set.stream().map( Node::path ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public NodeIds getIds()
    {
        return NodeIds.from( set.stream().map( Node::id ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<Node> nodes = new ImmutableSet.Builder<>();

        public Builder add( Node node )
        {
            nodes.add( node );
            return this;
        }

        public Builder addAll( Nodes nodes )
        {
            this.nodes.addAll( nodes.getSet() );
            return this;
        }

        public Nodes build()
        {
            return new Nodes( nodes.build() );
        }
    }
}
