package com.enonic.xp.core.node;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.core.support.AbstractImmutableEntitySet;

public class NodeIds
    extends AbstractImmutableEntitySet<NodeId>
{

    private NodeIds( final ImmutableSet<NodeId> set )
    {
        super( set );
    }

    private NodeIds( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.nodeIds ) );
    }

    public static NodeIds empty()
    {
        final ImmutableSet<NodeId> set = ImmutableSet.of();
        return new NodeIds( set );
    }

    public static NodeIds from( final NodeId... ids )
    {
        return new NodeIds( ImmutableSet.copyOf( ids ) );
    }

    public static NodeIds from( final String... ids )
    {
        return new NodeIds( parseIds( ids ) );
    }

    public static NodeIds from( final Iterable<NodeId> ids )
    {
        return new NodeIds( ImmutableSet.copyOf( ids ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSet<String> getAsStrings()
    {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for ( final NodeId nodeId : this.getSet() )
        {
            builder.add( nodeId.toString() );
        }

        return builder.build();
    }

    private static ImmutableSet<NodeId> parseIds( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<NodeId> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    private final static class ParseFunction
        implements Function<String, NodeId>
    {
        @Override
        public NodeId apply( final String value )
        {
            return NodeId.from( value );
        }
    }

    public static class Builder
    {
        private LinkedHashSet<NodeId> nodeIds = Sets.newLinkedHashSet();

        public Builder add( final NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public NodeIds build()
        {
            return new NodeIds( this );
        }

    }

}
