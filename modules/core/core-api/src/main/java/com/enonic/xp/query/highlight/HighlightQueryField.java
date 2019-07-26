package com.enonic.xp.query.highlight;

import com.google.common.base.Preconditions;

public class HighlightQueryField
{
    private final String name;

    private HighlightQueryField( final Builder builder )
    {
        this.name = builder.name;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public String getName()
    {
        return name;
    }

    public static class Builder
    {
        private String name;


        public Builder( final String name )
        {
            this.name = name;
        }

        private void validate()
        {
            Preconditions.checkNotNull( name, "name is required" );
        }

        public HighlightQueryField build()
        {
            validate();
            return new HighlightQueryField( this );
        }
    }
}
