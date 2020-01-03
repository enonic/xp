package com.enonic.xp.node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Nodes
    extends AbstractImmutableEntitySet<Node>
{
    private final ImmutableMap<NodeId, Node> map;

    private Nodes( final Set<Node> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = set.stream().collect( ImmutableMap.toImmutableMap( Node::id, Function.identity() ) );
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

    public Node getNodeById( final NodeId nodeId )
    {
        return this.map.get( nodeId );
    }

    public NodePaths getPaths()
    {
        return NodePaths.from( set.stream().map( Node::path ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public NodeIds getIds()
    {
        return NodeIds.from( map.keySet() );
    }

    public static class Builder
    {
        private final Set<Node> nodes = new LinkedHashSet<>();

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
            return new Nodes( nodes );
        }
    }
}
