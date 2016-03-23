package com.enonic.xp.page;


import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.Region;

@Beta
public final class Page
{
    private final DescriptorKey controller;

    private final PageTemplateKey template;

    private final PageRegions regions;

    private final Component fragment;

    private final PropertyTree config;

    private final boolean customized;

    private Page( final Builder builder )
    {
        Preconditions.checkArgument( !( builder.controller != null && builder.template != null ),
                                     "A Page cannot have both have a controller and a template set" );

        this.controller = builder.controller;
        this.template = builder.template;
        this.config = builder.config;
        this.regions = builder.regions;
        this.customized = builder.customized;
        this.fragment = builder.fragment;
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

    public boolean isCustomized()
    {
        return customized;
    }

    public Component getFragment()
    {
        return fragment;
    }

    public boolean isFragment()
    {
        return fragment != null;
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
            Objects.equals( regions, other.regions ) &&
            Objects.equals( fragment, other.fragment );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( controller, template, regions, config, fragment );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Page source )
    {
        return new Builder( source );
    }

    public Page copy()
    {
        return create( this ).build();
    }

    public static class Builder
    {
        private DescriptorKey controller;

        private PageTemplateKey template;

        private PageRegions regions;

        private Component fragment;

        private PropertyTree config;

        private boolean customized;

        private Builder()
        {
        }

        private Builder( final Page source )
        {
            this.template = source.template;
            this.controller = source.controller;
            this.regions = source.regions != null ? source.regions.copy() : null;
            this.config = source.config != null ? source.config.copy() : null;
            this.customized = source.customized;
            this.fragment = source.fragment;
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

        public Builder customized( final boolean customized )
        {
            this.customized = customized;
            return this;
        }

        public Builder fragment( final Component fragment )
        {
            this.fragment = fragment;
            return this;
        }

        public Page build()
        {
            return new Page( this );
        }
    }
}
