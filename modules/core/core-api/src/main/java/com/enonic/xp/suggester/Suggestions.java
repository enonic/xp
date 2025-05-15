package com.enonic.xp.suggester;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class Suggestions
    extends AbstractImmutableEntitySet<Suggestion>
{
    private static final Suggestions EMPTY = new Suggestions( ImmutableSet.of() );

    private Suggestions( final ImmutableSet<Suggestion> set )
    {
        super( set );
    }

    public static Suggestions empty()
    {
        return EMPTY;
    }

    public static Suggestions from( final Iterable<Suggestion> suggestions )
    {
        return fromInternal( ImmutableSet.copyOf( suggestions ) );
    }

    public static Suggestions from( final Suggestion... suggestions )
    {
        return fromInternal( ImmutableSet.copyOf( suggestions ) );
    }

    public Suggestion get( final String name )
    {
        return this.stream().filter( ( suggestion ) -> name.equals( suggestion.getName() ) ).findFirst().orElse( null );
    }

    private static Suggestions fromInternal( final ImmutableSet<Suggestion> set )
    {
        return set.isEmpty() ? EMPTY : new Suggestions( set );
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
            return fromInternal( ImmutableSet.copyOf( suggestions ) );
        }
    }
}
