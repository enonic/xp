package com.enonic.xp.node;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class NodePaths
    extends AbstractImmutableEntitySet<NodePath>
{
    private NodePaths( final ImmutableSet<NodePath> set )
    {
        super( set );
    }

    private NodePaths( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.nodePaths ) );
    }

    public static NodePaths empty()
    {
        final ImmutableSet<NodePath> set = ImmutableSet.of();
        return new NodePaths( set );
    }

    public static NodePaths from( final NodePath... paths )
    {
        return new NodePaths( ImmutableSet.copyOf( paths ) );
    }

    public static NodePaths from( final String... paths )
    {
        return new NodePaths( parsePaths( paths ) );
    }

    public static NodePaths from( final Iterable<NodePath> paths )
    {
        return new NodePaths( ImmutableSet.copyOf( paths ) );
    }

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
        private final Set<NodePath> nodePaths = new HashSet<>();

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
            return new NodePaths( this );
        }
    }

}
