package com.enonic.xp.suggester;

public final class TermSuggestionOption
    extends SuggestionOption
{
    private final Integer freq;

    private TermSuggestionOption( final Builder builder )
    {
        super( builder );
        this.freq = builder.freq;
    }

    public Integer getFreq()
    {
        return freq;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends SuggestionOption.Builder<Builder>
    {
        private Integer freq;

        private Builder()
        {
        }

        public Builder freq( final Integer freq )
        {
            this.freq = freq;
            return this;
        }

        @Override
        public TermSuggestionOption build()
        {
            return new TermSuggestionOption( this );
        }
    }
}
