package com.enonic.xp.query.suggester;

import com.google.common.base.Preconditions;

public abstract class SuggestionQuery
{
    private final String name;

    private final String field;

    private final String text;

    private final String analyzer;

    private final Integer size;

    protected SuggestionQuery( final SuggestionQuery.Builder builder )
    {
        Preconditions.checkNotNull( builder.name, "name is required" );
        Preconditions.checkNotNull( builder.field, "field is required" );
        Preconditions.checkNotNull( builder.text, "text is required" );

        this.name = builder.name;
        this.field = builder.field;
        this.text = builder.text;
        this.size = builder.size;
        this.analyzer = builder.analyzer;
    }

    public String getName()
    {
        return name;
    }

    public String getField()
    {
        return field;
    }

    public String getText()
    {
        return text;
    }

    public Integer getSize()
    {
        return size;
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public static class Builder<T extends SuggestionQuery.Builder>
    {
        private String name;

        private String field;

        private String text;

        private String analyzer;

        private Integer size;

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

        @SuppressWarnings("unchecked")
        public T field( final String field )
        {
            this.field = field;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T text( final String text )
        {
            this.text = text;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T size( final Integer size )
        {
            this.size = size;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return (T) this;
        }


    }
}
