package com.enonic.xp.query.suggester;

public abstract class SuggestionQuery
{
    private String name;

    @SuppressWarnings("unchecked")
    SuggestionQuery( final SuggestionQuery.Builder builder )
    {
        this.name = builder.name;
    }

    public String getName()
    {
        return name;
    }

    public static class Builder<T extends SuggestionQuery.Builder>
    {
        private String name;

        public Builder( final String name )
        {
            this.name = name;
        }

        @SuppressWarnings("unchecked")
        public T name( final String name )
        {
            this.name = name;
            return (T) this;
        }


    }
}
