package com.enonic.xp.node;

import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
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
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<NodePath> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    private final static class ParseFunction
        implements Function<String, NodePath>
    {
        @Override
        public NodePath apply( final String value )
        {
            return new NodePath( value );
        }
    }

    public static class Builder
    {
        private final Set<NodePath> nodePaths = Sets.newHashSet();

        public Builder addNodePath( final NodePath nodePath )
        {
            this.nodePaths.add( nodePath );
            return this;
        }

        public NodePaths build()
        {
            return new NodePaths( this );
        }
    }

}
