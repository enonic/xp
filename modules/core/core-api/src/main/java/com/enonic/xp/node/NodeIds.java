package com.enonic.xp.node;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
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
        return new NodeIds( parseIds( Lists.newArrayList( ids ) ) );
    }

    public static NodeIds from( final Iterable<NodeId> ids )
    {
        return new NodeIds( ImmutableSet.copyOf( ids ) );
    }

    public static NodeIds from( final Collection<String> ids )
    {
        return new NodeIds( parseIds( ids ) );
    }


    public static Builder create()
    {
        return new Builder();
    }

    private static ImmutableSet<NodeId> parseIds( final Collection<String> paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<NodeId> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    public List<String> getAsStrings()
    {
        return this.set.stream().map( NodeId::toString ).collect( Collectors.toList() );
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
        private final List<NodeId> nodeIds = Lists.newArrayList();

        public Builder add( final NodeId nodeId )
        {
            this.nodeIds.add( nodeId );
            return this;
        }

        public Builder addAll( final NodeIds nodeIds )
        {
            this.nodeIds.addAll( nodeIds.getSet() );
            return this;
        }


        public NodeIds build()
        {
            return new NodeIds( this );
        }

    }

}
