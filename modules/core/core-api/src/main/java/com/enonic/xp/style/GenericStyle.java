package com.enonic.xp.style;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class GenericStyle
    implements ElementStyle
{
    public static final String STYLE_ELEMENT_NAME = "style";

    private final String name;

    private final String displayName;

    private final String displayNameI18nKey;

    private GenericStyle( final Builder builder )
    {
        this.name = builder.name.trim();
        this.displayName = builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
    }

    @Override
    public String getElement()
    {
        return STYLE_ELEMENT_NAME;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getDisplayNameI18nKey()
    {
        return displayNameI18nKey;
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
        final GenericStyle that = (GenericStyle) o;
        return Objects.equals( name, that.name ) && Objects.equals( displayName, that.displayName ) &&
            Objects.equals( displayNameI18nKey, that.displayNameI18nKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, displayName, displayNameI18nKey );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "element", STYLE_ELEMENT_NAME ).
            add( "name", name ).
            add( "displayName", displayName ).
            add( "displayNameI18nKey", displayNameI18nKey ).
            toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String name;

        private String displayName;

        private String displayNameI18nKey;

        private Builder()
        {
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder displayNameI18nKey( final String displayNameI18nKey )
        {
            this.displayNameI18nKey = displayNameI18nKey;
            return this;
        }

        public GenericStyle build()
        {
            Preconditions.checkNotNull( this.name, "name cannot be null" );
            return new GenericStyle( this );
        }
    }
}
