package com.enonic.xp.suggester;

public final class TermSuggestionOption
    extends SuggestionOption
{
    private Integer freq;

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

    public static class Builder
        extends SuggestionOption.Builder<Builder>
    {
        private Integer freq;

        public Builder()
        {
            super();
        }

        @SuppressWarnings("unchecked")
        public Builder freq( final Integer freq )
        {
            this.freq = freq;
            return this;
        }

        public TermSuggestionOption build()
        {
            return new TermSuggestionOption( this );
        }

    }
}
