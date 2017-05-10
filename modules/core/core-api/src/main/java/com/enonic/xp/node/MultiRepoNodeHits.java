package com.enonic.xp.node;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

public class MultiRepoNodeHits
    extends AbstractImmutableEntityList<MultiRepoNodeHit>
{
    private MultiRepoNodeHits( final Collection<MultiRepoNodeHit> hits )
    {
        super( ImmutableList.copyOf( hits ) );
    }

    public static MultiRepoNodeHits empty()
    {
        final List<MultiRepoNodeHit> returnFields = Lists.newArrayList();
        return new MultiRepoNodeHits( returnFields );
    }

    private static MultiRepoNodeHits from( final Collection<MultiRepoNodeHit> returnFields )
    {
        return new MultiRepoNodeHits( returnFields );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<MultiRepoNodeHit> nodeHits = Lists.newArrayList();

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
