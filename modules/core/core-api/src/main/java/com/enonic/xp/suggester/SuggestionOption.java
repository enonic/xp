package com.enonic.xp.suggester;

public abstract class SuggestionOption
{
    private final String text;

    private final Float score;

    protected SuggestionOption( final Builder builder )
    {
        this.text = builder.text;
        this.score = builder.score;
    }

    public String getText()
    {
        return text;
    }

    public Float getScore()
    {
        return score;
    }

    public abstract static class Builder<T extends Builder>
    {
        private String text;

        private Float score;

        @SuppressWarnings("unchecked")
        public T text( final String text )
        {
            this.text = text;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T score( final Float score )
        {
            this.score = score;
            return (T) this;
        }

        public abstract SuggestionOption build();
    }
}
