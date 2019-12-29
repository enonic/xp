package com.enonic.xp.suggester;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public class Suggestions
    extends AbstractImmutableEntitySet<Suggestion>
{
    private Suggestions( final ImmutableSet<Suggestion> set )
    {
        super( set );
    }

    public static Suggestions empty()
    {
        final ImmutableSet<Suggestion> empty = ImmutableSet.of();
        return new Suggestions( empty );
    }

    public static Suggestions from( final ImmutableSet<Suggestion> suggestions )
    {
        return new Suggestions( suggestions );
    }

    public static Suggestions from( final Iterable<Suggestion> suggestions )
    {
        return from( ImmutableSet.copyOf( suggestions ) );
    }

    public static Suggestions from( final Suggestion... suggestions )
    {
        return from( ImmutableSet.copyOf( suggestions ) );
    }

    public Suggestion get( final String name )
    {
        return this.stream().
            filter( ( suggestion ) -> name.equals( suggestion.getName() ) ).
            findFirst().orElse( null );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<Suggestion> suggestions = new LinkedHashSet<>();

        public Builder add( final Suggestion suggestion )
        {
            this.suggestions.add( suggestion );
            return this;
        }

        public Suggestions build()
        {
            return new Suggestions( ImmutableSet.copyOf( suggestions ) );
        }
    }

}
