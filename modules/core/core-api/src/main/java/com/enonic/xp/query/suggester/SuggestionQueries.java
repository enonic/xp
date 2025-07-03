package com.enonic.xp.query.suggester;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class SuggestionQueries
    extends AbstractImmutableEntityList<SuggestionQuery>
{
    private static final SuggestionQueries EMPTY = new SuggestionQueries( ImmutableList.of() );

    private SuggestionQueries( final ImmutableList<SuggestionQuery> set )
    {
        super( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static SuggestionQueries empty()
    {
        return EMPTY;
    }

    public static SuggestionQueries fromCollection( final Collection<SuggestionQuery> suggestionQueries )
    {
        return fromInternal( ImmutableList.copyOf( suggestionQueries ) );
    }

    public static Collector<SuggestionQuery, ?, SuggestionQueries> collecting()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), SuggestionQueries::fromInternal );
    }

    private static SuggestionQueries fromInternal( final ImmutableList<SuggestionQuery> suggestionQueries )
    {
        return suggestionQueries.isEmpty() ? EMPTY : new SuggestionQueries( suggestionQueries );
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<SuggestionQuery> suggestionQueries = ImmutableList.builder();

        public Builder add( final SuggestionQuery suggestionQuery )
        {
            this.suggestionQueries.add( suggestionQuery );
            return this;
        }

        public SuggestionQueries build()
        {
            return fromInternal( this.suggestionQueries.build() );
        }
    }
}
