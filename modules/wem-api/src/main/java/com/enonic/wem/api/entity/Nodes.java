package com.enonic.wem.api.entity;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class Nodes
    extends AbstractImmutableEntitySet<Node>
{
    private final ImmutableMap<EntityId, Node> map;

    private Nodes( final Set<Node> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = Maps.uniqueIndex( set, new ToIdFunction() );
    }

    private final static class ToIdFunction
        implements Function<Node, EntityId>
    {
        @Override
        public EntityId apply( final Node value )
        {
            return value.id();
        }
    }

    public Node getNodeById( final EntityId entityId )
    {
        return this.map.get( entityId );
    }

    public NodePaths getPaths()
    {
        final Collection<NodePath> paths = Collections2.transform( this.set, new ToKeyFunction() );
        return NodePaths.from( paths );
    }


    public static Nodes empty()
    {
        final ImmutableSet<Node> set = ImmutableSet.of();
        return new Nodes( set );
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

    private final static class ToKeyFunction
        implements Function<Node, NodePath>
    {
        @Override
        public NodePath apply( final Node value )
        {
            return value.path();
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Set<Node> nodes = Sets.newLinkedHashSet();

        public Builder add( Node node )
        {
            nodes.add( node );
            return this;
        }

        public Nodes build()
        {
            return new Nodes( nodes );
        }
    }
}
