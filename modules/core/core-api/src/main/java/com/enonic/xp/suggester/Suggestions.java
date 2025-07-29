package com.enonic.xp.suggester;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class Suggestions
    extends AbstractImmutableEntityList<Suggestion>
{
    private static final Suggestions EMPTY = new Suggestions( ImmutableList.of() );

    private Suggestions( final ImmutableList<Suggestion> set )
    {
        super( set );
    }

    public static Suggestions empty()
    {
        return EMPTY;
    }

    public static Suggestions from( final Iterable<Suggestion> suggestions )
    {
        return suggestions instanceof Suggestions s ? s : fromInternal( ImmutableList.copyOf( suggestions ) );
    }

    public static Suggestions from( final Suggestion... suggestions )
    {
        return fromInternal( ImmutableList.copyOf( suggestions ) );
    }

    public Suggestion get( final String name )
    {
        return this.stream().filter( ( suggestion ) -> name.equals( suggestion.getName() ) ).findFirst().orElse( null );
    }

    public static Collector<Suggestion, ?, Suggestions> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), Suggestions::fromInternal );
    }

    private static Suggestions fromInternal( final ImmutableList<Suggestion> set )
    {
        return set.isEmpty() ? EMPTY : new Suggestions( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<Suggestion> suggestions = ImmutableList.builder();

        public Builder add( final Suggestion suggestion )
        {
            this.suggestions.add( suggestion );
            return this;
        }

        public Suggestions build()
        {
            return fromInternal( suggestions.build() );
        }
    }
}
