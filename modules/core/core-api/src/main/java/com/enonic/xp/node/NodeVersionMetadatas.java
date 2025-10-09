package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class NodeVersionMetadatas
    extends AbstractImmutableEntityList<NodeVersionMetadata>
{
    private static final NodeVersionMetadatas EMPTY = new NodeVersionMetadatas( ImmutableList.of() );

    private NodeVersionMetadatas( final ImmutableList<NodeVersionMetadata> nodeVersionMetadatas )
    {
        super(nodeVersionMetadatas);
    }

    public NodeVersionIds getAllVersionIds()
    {
        return stream().map( NodeVersionMetadata::getNodeVersionId ).collect( NodeVersionIds.collector() );
    }

    public static NodeVersionMetadatas empty()
    {
        return EMPTY;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Collector<NodeVersionMetadata, ?, NodeVersionMetadatas> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), NodeVersionMetadatas::fromInternal );
    }

    private static NodeVersionMetadatas fromInternal( ImmutableList<NodeVersionMetadata> nodeVersionMetadatas )
    {
        return nodeVersionMetadatas.isEmpty() ? EMPTY : new NodeVersionMetadatas( nodeVersionMetadatas );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<NodeVersionMetadata> nodeVersionMetadata = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final NodeVersionMetadata nodeVersionMetadata )
        {
            this.nodeVersionMetadata.add( nodeVersionMetadata );
            return this;
        }

        public Builder addAll( final Iterable<NodeVersionMetadata> nodeVersionMetadata )
        {
            this.nodeVersionMetadata.addAll( nodeVersionMetadata );
            return this;
        }

        public NodeVersionMetadatas build()
        {
            return fromInternal( nodeVersionMetadata.build() );
        }
    }
}
