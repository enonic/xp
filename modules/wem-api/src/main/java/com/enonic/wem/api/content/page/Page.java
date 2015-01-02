package com.enonic.wem.api.content.page;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class Page
    implements com.enonic.wem.api.rendering.Component
{
    private final DescriptorKey controller;

    private final PageTemplateKey template;

    private final PageRegions regions;

    private final PropertyTree config;

    private Page( final PageProperties properties )
    {
        Preconditions.checkNotNull( properties.config, "config cannot be null" );
        this.controller = properties.controller;
        this.template = properties.template;
        this.config = properties.config;
        this.regions = properties.regions;
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

        final Page page = (Page) o;

        if ( !config.equals( page.config ) )
        {
            return false;
        }
        if ( controller != null ? !controller.equals( page.controller ) : page.controller != null )
        {
            return false;
        }
        if ( !regions.equals( page.regions ) )
        {
            return false;
        }
        if ( template != null ? !template.equals( page.template ) : page.template != null )
        {
            return false;
        }

        return true;
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

    static class PageProperties
    {
        DescriptorKey controller;

        PageTemplateKey template;

        PageRegions regions;

        PropertyTree config;

        PageProperties()
        {
            // nothing
        }

        PageProperties( final Page source )
        {
            this.template = source.template;
            this.controller = source.controller;
            this.regions = source.regions != null ? source.regions.copy() : null;
            this.config = source.config != null ? source.config.copy() : null;
        }

        public PageProperties controller( DescriptorKey value )
        {
            this.controller = value;
            return this;
        }

        public PageProperties template( PageTemplateKey value )
        {
            this.template = value;
            return this;
        }
    }

    public static PageEditBuilder editPage( final Page toBeEdited )
    {
        return new PageEditBuilder( toBeEdited );
    }

    public static class PageEditBuilder
        extends PageProperties
        implements EditBuilder<Page>
    {
        private final Page original;

        private final Changes.Builder changes = new Changes.Builder();

        public PageEditBuilder( final Page original )
        {
            super( original );
            this.original = original;
        }

        public PageEditBuilder controller( DescriptorKey value )
        {
            changes.recordChange( newPossibleChange( "controller" ).from( this.original.getTemplate() ).to( value ).build() );
            this.controller = value;
            return this;
        }

        public PageEditBuilder template( PageTemplateKey value )
        {
            changes.recordChange( newPossibleChange( "template" ).from( this.original.getTemplate() ).to( value ).build() );
            this.template = value;
            return this;
        }

        public PageEditBuilder regions( final PageRegions value )
        {
            changes.recordChange( newPossibleChange( "regions" ).from( this.original.getRegions() ).to( value ).build() );
            this.regions = value;
            return this;
        }

        public PageEditBuilder config( PropertyTree value )
        {
            changes.recordChange( newPossibleChange( "config" ).from( original.getConfig() ).to( value ).build() );
            config = value;
            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
        }


        public Page build()
        {
            return new Page( this );
        }

    }

    public static class Builder
        extends PageProperties
    {
        private Builder()
        {
            this.config = new PropertyTree();
        }

        private Builder( final Page page )
        {
            super( page );
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
