package com.enonic.xp.suggester;

public class TermSuggestion
    extends Suggestion<TermSuggestionEntry>
{
    private TermSuggestion( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends Suggestion.Builder<Builder, TermSuggestionEntry>
    {
        public Builder( final String name )
        {
            super( name );
        }

        @Override
        public TermSuggestion build()
        {
            return new TermSuggestion( this );
        }

    }


}
