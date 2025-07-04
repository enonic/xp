package com.enonic.xp.node;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class NodePaths
    extends AbstractImmutableEntitySet<NodePath>
{
    private static final NodePaths EMPTY = new NodePaths( ImmutableSet.of() );

    private NodePaths( final ImmutableSet<NodePath> set )
    {
        super( set );
    }

    public static NodePaths empty()
    {
        return EMPTY;
    }

    public static NodePaths from( final NodePath... paths )
    {
        return fromInternal( ImmutableSet.copyOf( paths ) );
    }

    public static NodePaths from( final String... paths )
    {
        return fromInternal(
            Arrays.stream( paths ).map( NodePath::new ).collect( ImmutableSet.toImmutableSet() ) );
    }

    public static NodePaths from( final Iterable<NodePath> paths )
    {
        return paths instanceof NodePaths ? (NodePaths) paths : fromInternal( ImmutableSet.copyOf( paths ) );
    }

    public static Collector<NodePath, ?, NodePaths> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), NodePaths::fromInternal );
    }

    private static NodePaths fromInternal( final ImmutableSet<NodePath> nodePaths )
    {
        return nodePaths.isEmpty() ? EMPTY : new NodePaths( nodePaths );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<NodePath> nodePaths = ImmutableSet.builder();

        public Builder addNodePath( final NodePath nodePath )
        {
            this.nodePaths.add( nodePath );
            return this;
        }

        public Builder addNodePaths( final Iterable<? extends NodePath> nodePaths )
        {
            this.nodePaths.addAll( nodePaths );
            return this;
        }

        public NodePaths build()
        {
            return fromInternal( nodePaths.build() );
        }
    }

}
