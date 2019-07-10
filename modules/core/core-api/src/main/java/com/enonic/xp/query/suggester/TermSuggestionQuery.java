package com.enonic.xp.query.suggester;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class TermSuggestionQuery
    extends SuggestionQuery
{
    private final String field;

    private final String text;

    private TermSuggestionQuery( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.field, "field is required" );
        Preconditions.checkNotNull( builder.text, "text is required" );
        this.field = builder.field;
        this.text = builder.text;
    }

    public String getField()
    {
        return field;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "field", getField() ).
            add( "text", getText() ).
            toString();
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends SuggestionQuery.Builder<Builder>
    {
        private String field;

        private String text;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder field( final String field )
        {
            this.field = field;
            return this;
        }

        public Builder text( final String text )
        {
            this.text = text;
            return this;
        }

        public TermSuggestionQuery build()
        {
            return new TermSuggestionQuery( this );
        }
    }
}
