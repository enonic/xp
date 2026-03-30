package com.enonic.xp.node;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class SearchTargets
    extends AbstractImmutableEntityList<SearchTarget>
{
    private SearchTargets( final ImmutableList<SearchTarget> list )
    {
        super( list );
    }

    public static SearchTargets from( final Iterable<SearchTarget> searchTargets )
    {
        return searchTargets instanceof SearchTargets s ? s : new SearchTargets( ImmutableList.copyOf( searchTargets ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<SearchTarget> targets = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder add( final SearchTarget target )
        {
            this.targets.add( target );
            return this;
        }

        public SearchTargets build()
        {
            return new SearchTargets( targets.build().asList() );
        }
    }
}
