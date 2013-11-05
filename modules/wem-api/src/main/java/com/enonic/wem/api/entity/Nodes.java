package com.enonic.wem.api.entity;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class Nodes
    extends AbstractImmutableEntityList<Node>
{
    private Nodes( final ImmutableList<Node> list )
    {
        super( list );
    }

    public NodePaths getPaths()
    {
        final Collection<NodePath> paths = Collections2.transform( this.list, new ToKeyFunction() );
        return NodePaths.from( paths );
    }

    public static Nodes empty()
    {
        final ImmutableList<Node> list = ImmutableList.of();
        return new Nodes( list );
    }

    public static Nodes from( final Node... nodes )
    {
        return new Nodes( ImmutableList.copyOf( nodes ) );
    }

    public static Nodes from( final Iterable<? extends Node> nodes )
    {
        return new Nodes( ImmutableList.copyOf( nodes ) );
    }

    public static Nodes from( final Collection<? extends Node> nodes )
    {
        return new Nodes( ImmutableList.copyOf( nodes ) );
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

    public static Builder newNodes()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<Node> builder = ImmutableList.builder();

        public Builder add( Node node )
        {
            builder.add( node );
            return this;
        }

        public Nodes build()
        {
            return new Nodes( builder.build() );
        }
    }
}
