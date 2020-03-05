package com.enonic.xp.project.layer;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ContentLayerName
{
    public static final ContentLayerName DEFAULT_LAYER_NAME = ContentLayerName.from( "base" );

    public static final String VALID_NAME_REGEX = "[A-Za-z0-9\\-_]+";

    private final String value;

    private ContentLayerName( final Builder builder )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( builder.value ), "Layer name cannot be null or empty" );
        Preconditions.checkArgument( builder.value.matches( "^" + VALID_NAME_REGEX + "$" ),
                                     "Layer name format incorrect: " + builder.value );
        this.value = builder.value;
    }

    public static ContentLayerName from( final String name )
    {
        return ContentLayerName.create().
            value( name ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getValue()
    {
        return value;
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

    public boolean isDefault()
    {
        return DEFAULT_LAYER_NAME.equals( this );
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
