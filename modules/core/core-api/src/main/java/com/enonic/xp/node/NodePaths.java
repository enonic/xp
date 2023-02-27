package com.enonic.xp.node;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class NodePaths
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
        return fromInternal( parsePaths( paths ) );
    }

    public static NodePaths from( final Iterable<NodePath> paths )
    {
        return fromInternal( ImmutableSet.copyOf( paths ) );
    }

    private static NodePaths fromInternal( final ImmutableSet<NodePath> nodePaths )
    {
        return nodePaths.isEmpty() ? EMPTY : new NodePaths( nodePaths );
    }

    @Deprecated
    public ImmutableSet<String> getAsStrings()
    {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for ( final NodePath nodePath : this.getSet() )
        {
            builder.add( nodePath.toString() );
        }

        return builder.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    private static ImmutableSet<NodePath> parsePaths( final String... paths )
    {
        return Arrays.stream( paths ).map( NodePath::new ).collect( ImmutableSet.toImmutableSet() );
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<NodePath> nodePaths = ImmutableSet.builder();

        public Builder addNodePath( final NodePath nodePath )
        {
            this.nodePaths.add( nodePath );
            return this;
        }

        public Builder addNodePaths( final Collection<NodePath> nodePaths )
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
