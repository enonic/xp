package com.enonic.xp.query.suggester;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class SuggestionQueries
    extends AbstractImmutableEntitySet<SuggestionQuery>
{
    private SuggestionQueries( final ImmutableSet<SuggestionQuery> set )
    {
        super( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static SuggestionQueries empty()
    {
        return new SuggestionQueries( ImmutableSet.of() );
    }

    public static SuggestionQueries fromCollection( final Collection<SuggestionQuery> suggestionQueries )
    {
        return new SuggestionQueries( ImmutableSet.copyOf( suggestionQueries ) );
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<SuggestionQuery> suggestionQueries = ImmutableSet.builder();

        public Builder add( final SuggestionQuery suggestionQuery )
        {
            this.suggestionQueries.add( suggestionQuery );
            return this;
        }

        public SuggestionQueries build()
        {
            return new SuggestionQueries( this.suggestionQueries.build() );
        }
    }
}
