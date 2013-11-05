package com.enonic.wem.api.entity;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class NodePaths
    extends AbstractImmutableEntitySet<NodePath>
{
    private NodePaths( final ImmutableSet<NodePath> set )
    {
        super( set );
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
}
