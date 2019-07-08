package com.enonic.xp.query.suggester;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public class SuggestionQueries
    extends AbstractImmutableEntitySet<SuggestionQuery>
{
    private SuggestionQueries( final ImmutableSet<SuggestionQuery> set )
    {
        super( set );
    }

    private SuggestionQueries( final Set<SuggestionQuery> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static SuggestionQueries empty()
    {
        final Set<SuggestionQuery> returnFields = Sets.newHashSet();
        return new SuggestionQueries( returnFields );
    }

    public static SuggestionQueries fromCollection( final Collection<SuggestionQuery> suggestionQueries )
    {
        return new SuggestionQueries( ImmutableSet.copyOf( suggestionQueries ) );
    }

    public static final class Builder
    {
        private final Set<SuggestionQuery> suggestionQueries = Sets.newHashSet();

        public Builder add( final SuggestionQuery suggestionQuery )
        {
            this.suggestionQueries.add( suggestionQuery );
            return this;
        }

        public SuggestionQueries build()
        {
            return new SuggestionQueries( ImmutableSet.copyOf( this.suggestionQueries ) );
        }
    }
}
