package com.enonic.xp.style;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

public final class StyleDescriptor
{
    private final ApplicationKey applicationKey;

    private final String cssPath;

    private final ImmutableList<ElementStyle> elements;

    private StyleDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.application, "applicationKey cannot be null" );
        this.applicationKey = builder.application;
        this.cssPath = builder.cssPath;
        this.elements = builder.elements.build();
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getCssPath()
    {
        return cssPath;
    }

    public ImmutableList<ElementStyle> getElements()
    {
        return elements;
    }

    public static ResourceKey toResourceKey( final ApplicationKey key )
    {
        return ResourceKey.from( key, "site/styles.xml" );
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
            Objects.equals( elements, that.elements );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, cssPath, elements );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "applicationKey", applicationKey ).
            add( "cssPath", cssPath ).
            add( "elements", elements ).
            toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private ApplicationKey application;

        private String cssPath;

        private ImmutableList.Builder<ElementStyle> elements;

        private Set<String> elementNames;

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

        public StyleDescriptor build()
        {
            return new StyleDescriptor( this );
        }
    }
}
