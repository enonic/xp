package com.enonic.wem.api.content.page;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.region.Component;
import com.enonic.wem.api.content.page.region.ComponentPath;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.PropertyTree;

public final class Page
    implements com.enonic.wem.api.rendering.Component
{
    private final DescriptorKey controller;

    private final PageTemplateKey template;

    private final PageRegions regions;

    private final PropertyTree config;

    private Page( final Builder builder )
    {
        Preconditions.checkArgument( !( builder.controller != null && builder.template != null ),
                                     "A Page cannot have both have a controller and a template set" );

        this.controller = builder.controller;
        this.template = builder.template;
        this.config = builder.config;
        this.regions = builder.regions;
    }

    public boolean hasController()
    {
        return controller != null;
    }

    public DescriptorKey getController()
    {
        return controller;
    }

    public boolean hasTemplate()
    {
        return template != null;
    }

    public PageTemplateKey getTemplate()
    {
        return template;
    }

    public boolean hasRegions()
    {
        return regions != null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Region getRegion( final String name )
    {
        return this.regions.getRegion( name );
    }

    public PageRegions getRegions()
    {
        return regions;
    }

    public boolean hasConfig()
    {
        return config != null;
    }

    public PropertyTree getConfig()
    {
        return config;
    }

    public Component getComponent( final ComponentPath path )
    {
        return regions.getComponent( path );
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

        final Page other = (Page) o;

        return Objects.equals( template, other.template ) &&
            Objects.equals( controller, other.controller ) &&
            Objects.equals( config, other.config ) &&
            Objects.equals( regions, other.regions );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( controller, template, regions, config );
    }

    public static Builder newPage()
    {
        return new Builder();
    }

    public static Builder newPage( final Page source )
    {
        return new Builder( source );
    }

    public Page copy()
    {
        return newPage( this ).build();
    }

    public static class Builder
    {
        private DescriptorKey controller;

        private PageTemplateKey template;

        private PageRegions regions;

        private PropertyTree config;

        private Builder()
        {
            // Default
        }

        private Builder( final Page source )
        {
            this.template = source.template;
            this.controller = source.controller;
            this.regions = source.regions != null ? source.regions.copy() : null;
            this.config = source.config != null ? source.config.copy() : null;
        }

        public Builder regions( final PageRegions value )
        {
            this.regions = value;
            return this;
        }

        public Builder controller( final DescriptorKey value )
        {
            this.controller = value;
            return this;
        }

        public Builder template( final PageTemplateKey value )
        {
            this.template = value;
            return this;
        }

        public Builder config( final PropertyTree config )
        {
            this.config = config;
            return this;
        }

        public Page build()
        {
            return new Page( this );
        }
    }
}
