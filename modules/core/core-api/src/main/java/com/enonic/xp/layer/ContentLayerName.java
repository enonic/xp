package com.enonic.xp.layer;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

@Beta
public final class ContentLayerName
{
    private static final String VALID_REPOSITORY_ID_REGEX = "([a-zA-Z0-9\\-:])([a-zA-Z0-9_\\-\\.:])*";

    private final String value;

    private ContentLayerName( final Builder builder )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( builder.value ), "name cannot be null or empty" );
        Preconditions.checkArgument( builder.value.matches( "^" + VALID_REPOSITORY_ID_REGEX + "$" ),
                                     "name format incorrect: " + builder.value );
        this.value = builder.value;
    }

    public static ContentLayerName from( final String name )
    {
        return ContentLayerName.create().
            value( name ).
            build();
    }

    public String getValue()
    {
        return value;
    }


    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ContentLayerName branch = (ContentLayerName) o;
        return value.equals( branch.value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    public static final class Builder
    {
        private String value;

        private Builder()
        {
        }

        public Builder value( String value )
        {
            this.value = value;
            return this;
        }

        public ContentLayerName build()
        {
            return new ContentLayerName( this );
        }
    }
}


