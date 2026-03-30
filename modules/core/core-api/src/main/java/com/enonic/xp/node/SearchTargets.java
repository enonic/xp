package com.enonic.xp.node;

import com.google.common.collect.ImmutableList;

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
        private final ImmutableList.Builder<SearchTarget> targets = ImmutableList.builder();

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
            return new SearchTargets( targets.build() );
        }
    }
}
