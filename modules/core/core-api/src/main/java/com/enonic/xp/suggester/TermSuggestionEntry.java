package com.enonic.xp.suggester;

public final class TermSuggestionEntry
    extends SuggestionEntry<TermSuggestionOption>
{
    private TermSuggestionEntry( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends SuggestionEntry.Builder<Builder, TermSuggestionOption>
    {
        @Override
        public TermSuggestionEntry build()
        {
            return new TermSuggestionEntry( this );
        }
    }
}
