package com.enonic.xp.style;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;

import static java.util.Objects.requireNonNull;

public final class StyleDescriptor
{
    private final ApplicationKey applicationKey;

    private final ImmutableList<Style> elements;

    private final Instant modifiedTime;

    private StyleDescriptor( final Builder builder )
    {
        this.applicationKey = requireNonNull( builder.application, "applicationKey is required" );
        this.elements = builder.elements.build();
        this.modifiedTime = builder.modifiedTime;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public List<Style> getElements()
    {
        return elements;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
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
        return Objects.equals( applicationKey, that.applicationKey ) &&
            Objects.equals( elements, that.elements ) && Objects.equals( modifiedTime, that.modifiedTime );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, elements, modifiedTime );
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
            .add( "elements", elements )
            .add( "modifiedTime", modifiedTime )
            .toString();
    }

    public static final class Builder
    {
        private ApplicationKey application;

        private Instant modifiedTime;

        private final ImmutableList.Builder<Style> elements;

        private final Set<String> elementNames;

        private Builder( final StyleDescriptor styleDescriptor )
        {
            this();
            styleDescriptor.getElements().forEach( this::addStyleElement );
            this.application = styleDescriptor.applicationKey;
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

        public Builder modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder addStyleElement( final Style element )
        {
            if ( this.elementNames.contains( element.getName() ) )
            {
                throw new IllegalArgumentException( "Duplicated style element name: " + element.getName() );
            }
            this.elementNames.add( element.getName() );
            this.elements.add( element );
            return this;
        }

        public Builder addStyleElements( final Iterable<? extends Style> elements )
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
