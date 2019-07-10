package com.enonic.xp.query.suggester;

import com.google.common.base.MoreObjects;

public final class TermSuggestionQuery
    extends SuggestionQuery
{
    private final String field;

    private TermSuggestionQuery( final Builder builder )
    {
        super( builder );
        this.field = builder.field;
    }

    public String getField()
    {
        return field;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "field", getField() ).
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

        public Builder( final String name )
        {
            super( name );
        }

        public Builder field( final String field )
        {
            this.field = field;
            return this;
        }

        public TermSuggestionQuery build()
        {
            return new TermSuggestionQuery( this );
        }
    }
}
