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

    public static class Builder
        extends SuggestionEntry.Builder<Builder, TermSuggestionOption>
    {
        public TermSuggestionEntry build()
        {
            return new TermSuggestionEntry( this );
        }
    }
}
