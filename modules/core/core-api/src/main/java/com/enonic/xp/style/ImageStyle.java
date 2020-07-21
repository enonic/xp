package com.enonic.xp.style;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public final class ImageStyle
    implements ElementStyle
{
    public static final String STYLE_ELEMENT_NAME = "image";

    private final String name;

    private final String displayName;

    private final String displayNameI18nKey;

    private final String aspectRatio;

    private final String filter;

    private ImageStyle( final Builder builder )
    {
        this.name = builder.name.trim();
        this.displayName = builder.displayName;
        this.displayNameI18nKey = builder.displayNameI18nKey;
        this.aspectRatio = builder.aspectRatio;
        this.filter = builder.filter;
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

    public String getAspectRatio()
    {
        return aspectRatio;
    }

    public String getFilter()
    {
        return filter;
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
        final ImageStyle that = (ImageStyle) o;
        return Objects.equals( name, that.name ) && Objects.equals( displayName, that.displayName ) &&
            Objects.equals( displayNameI18nKey, that.displayNameI18nKey ) && Objects.equals( aspectRatio, that.aspectRatio ) &&
            Objects.equals( filter, that.filter );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, displayName, displayNameI18nKey, aspectRatio, filter );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "element", STYLE_ELEMENT_NAME ).
            add( "name", name ).
            add( "displayName", displayName ).
            add( "displayNameI18nKey", displayNameI18nKey ).
            add( "aspectRatio", aspectRatio ).
            add( "filter", filter ).
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

        private String aspectRatio;

        private String filter;

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

        public Builder aspectRatio( final String aspectRatio )
        {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public Builder filter( final String filter )
        {
            this.filter = filter;
            return this;
        }

        public ImageStyle build()
        {
            Preconditions.checkNotNull( this.name, "name cannot be null" );
            return new ImageStyle( this );
        }
    }
}
