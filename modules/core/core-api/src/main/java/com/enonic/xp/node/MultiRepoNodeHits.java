package com.enonic.xp.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class MultiRepoNodeHits
    extends AbstractImmutableEntityList<MultiRepoNodeHit>
{
    private MultiRepoNodeHits( final Collection<MultiRepoNodeHit> hits )
    {
        super( ImmutableList.copyOf( hits ) );
    }

    public static MultiRepoNodeHits empty()
    {
        final List<MultiRepoNodeHit> returnFields = new ArrayList<>();
        return new MultiRepoNodeHits( returnFields );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<MultiRepoNodeHit> nodeHits = new ArrayList<>();

        public Builder add( final MultiRepoNodeHit nodeHit )
        {
            this.nodeHits.add( nodeHit );
            return this;
        }

        public MultiRepoNodeHits build()
        {
            return new MultiRepoNodeHits( this.nodeHits );
        }
    }

}
