package com.enonic.xp.node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class MultiRepoNodeHits
    extends AbstractImmutableEntityList<MultiRepoNodeHit>
{
    private static final MultiRepoNodeHits EMPTY = new MultiRepoNodeHits( ImmutableList.of() );

    private MultiRepoNodeHits( final ImmutableList<MultiRepoNodeHit> hits )
    {
        super( hits );
    }

    public static MultiRepoNodeHits empty()
    {
        return EMPTY;
    }

    public static Collector<MultiRepoNodeHit, ?, MultiRepoNodeHits> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), MultiRepoNodeHits::new );
    }

    private static MultiRepoNodeHits fromInternal( final ImmutableList<MultiRepoNodeHit> hits )
    {
        return hits.isEmpty() ? EMPTY : new MultiRepoNodeHits( hits );
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<MultiRepoNodeHit> nodeHits = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder add( final MultiRepoNodeHit nodeHit )
        {
            this.nodeHits.add( nodeHit );
            return this;
        }

        public MultiRepoNodeHits build()
        {
            return fromInternal( this.nodeHits.build() );
        }
    }

}
