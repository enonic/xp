package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class NodeVersions
    extends AbstractImmutableEntityList<NodeVersion>
{
    private static final NodeVersions EMPTY = new NodeVersions( ImmutableList.of() );

    private NodeVersions( final ImmutableList<NodeVersion> nodeVersions )
    {
        super( nodeVersions );
    }

    public NodeVersionIds getAllVersionIds()
    {
        return stream().map( NodeVersion::getNodeVersionId ).collect( NodeVersionIds.collector() );
    }

    public static NodeVersions empty()
    {
        return EMPTY;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Collector<NodeVersion, ?, NodeVersions> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), NodeVersions::fromInternal );
    }

    private static NodeVersions fromInternal( ImmutableList<NodeVersion> nodeVersions )
    {
        return nodeVersions.isEmpty() ? EMPTY : new NodeVersions( nodeVersions );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<NodeVersion> nodeVersions = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final NodeVersion nodeVersion )
        {
            this.nodeVersions.add( nodeVersion );
            return this;
        }

        public Builder addAll( final Iterable<NodeVersion> nodeVersions )
        {
            this.nodeVersions.addAll( nodeVersions );
            return this;
        }

        public NodeVersions build()
        {
            return fromInternal( nodeVersions.build() );
        }
    }
}
