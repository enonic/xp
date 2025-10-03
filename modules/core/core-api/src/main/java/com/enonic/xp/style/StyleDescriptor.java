package com.enonic.xp.style;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

public final class StyleDescriptor
{
    private final ApplicationKey applicationKey;

    private final String cssPath;

    private final ImmutableList<ElementStyle> elements;

    private final Instant modifiedTime;

    private StyleDescriptor( final Builder builder )
    {
        this.applicationKey = Objects.requireNonNull( builder.application, "applicationKey is required" );
        this.cssPath = builder.cssPath;
        this.elements = builder.elements.build();
        this.modifiedTime = builder.modifiedTime;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getCssPath()
    {
        return cssPath;
    }

    public List<ElementStyle> getElements()
    {
        return elements;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public static ResourceKey toResourceKey( final ApplicationKey key )
    {
        return ResourceKey.from( key, "site/styles/image.yml" );
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
        final StyleDescriptor that = (StyleDescriptor) o;
        return Objects.equals( applicationKey, that.applicationKey ) && Objects.equals( cssPath, that.cssPath ) &&
            Objects.equals( elements, that.elements ) && Objects.equals( modifiedTime, that.modifiedTime );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, cssPath, elements, modifiedTime );
    }

    public static Builder copyOf( final StyleDescriptor styleDescriptor )
    {
        return new StyleDescriptor.Builder( styleDescriptor );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "applicationKey", applicationKey )
            .add( "cssPath", cssPath )
            .add( "elements", elements )
            .add( "modifiedTime", modifiedTime )
            .toString();
    }

    public static final class Builder
    {
        private ApplicationKey application;

        private String cssPath;

        private Instant modifiedTime;

        private final ImmutableList.Builder<ElementStyle> elements;

        private final Set<String> elementNames;

        private Builder( final StyleDescriptor styleDescriptor )
        {
            this();
            styleDescriptor.getElements().forEach( this::addStyleElement );
            this.application = styleDescriptor.applicationKey;
            this.cssPath = styleDescriptor.cssPath;
            this.modifiedTime = styleDescriptor.modifiedTime;
        }

        private Builder()
        {
            this.elements = new ImmutableList.Builder<>();
            this.elementNames = new HashSet<>();
        }

        public Builder application( final ApplicationKey applicationKey )
        {
            this.application = applicationKey;
            return this;
        }

        public Builder cssPath( final String cssPath )
        {
            this.cssPath = cssPath;
            return this;
        }

        public Builder modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder addStyleElement( final ElementStyle element )
        {
            if ( this.elementNames.contains( element.getName() ) )
            {
                throw new IllegalArgumentException( "Duplicated style element name: " + element.getName() );
            }
            this.elementNames.add( element.getName() );
            this.elements.add( element );
            return this;
        }

        public Builder addStyleElements( final Iterable<ElementStyle> elements )
        {
            elements.forEach( this::addStyleElement );
            return this;
        }

        public StyleDescriptor build()
        {
            return new StyleDescriptor( this );
        }
    }
}
