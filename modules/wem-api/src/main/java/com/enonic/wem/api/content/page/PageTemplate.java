package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class PageTemplate
{
    private final PageTemplateKey key;

    private final ResourcePath parentPath;

    private final ResourcePath path;

    private final String displayName;

    private final PageDescriptorKey descriptor;

    private final RootDataSet config;

    private final PageRegions regions;

    private final ContentTypeNames canRender;

    private PageTemplate( final PageTemplateProperties properties )
    {
        this.key = resolveKey( properties );
        this.parentPath = properties.parentPath;
        this.path = ResourcePath.from( this.parentPath, this.key.getTemplateName().toString() );
        this.displayName = properties.displayName;
        this.descriptor = properties.descriptor;
        this.config = properties.config;
        this.canRender = properties.canRender != null ? properties.canRender : ContentTypeNames.empty();
        this.regions = properties.regions;
    }

    private PageTemplateKey resolveKey( final PageTemplateProperties properties )
    {
        if ( properties.key != null )
        {
            return properties.key;
        }
        else
        {
            Preconditions.checkNotNull( properties.moduleName, "moduleKey cannot be null when key is not given" );
            Preconditions.checkNotNull( properties.name, "name cannot be null when key is not given" );
            return PageTemplateKey.from( properties.moduleName, properties.name );
        }
    }

    public PageTemplateKey getKey()
    {
        return key;
    }

    public PageTemplateName getName()
    {
        return this.key.getTemplateName();
    }

    public ResourcePath getParentPath()
    {
        return parentPath;
    }

    public ResourcePath getPath()
    {
        return path;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public PageDescriptorKey getDescriptor()
    {
        return descriptor;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public ContentTypeNames getCanRender()
    {
        return canRender;
    }

    public boolean hasRegions()
    {
        return regions != null;
    }

    public PageRegions getRegions()
    {
        return regions;
    }

    public static PageTemplate.Builder newPageTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends PageTemplateProperties
    {
        private Builder()
        {
        }

        public Builder key( final PageTemplateKey key )
        {
            this.key = key;
            return this;
        }

        /**
         * Optional. Only required when key is not given.
         */
        public Builder module( final ModuleName value )
        {

            this.moduleName = value;
            return this;
        }

        /**
         * Optional. Only required when key is not given.
         */
        public Builder name( final PageTemplateName name )
        {
            this.name = name;
            return this;
        }

        /**
         * Optional. Only required when key is not given.
         */
        public Builder name( final String name )
        {
            this.name = PageTemplateName.from( name );
            return this;
        }

        public Builder parentPath( final ResourcePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder descriptor( final PageDescriptorKey descriptor )
        {
            this.descriptor = descriptor;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder canRender( final ContentTypeNames canRender )
        {
            this.canRender = canRender;
            return this;
        }

        public Builder regions( final PageRegions value )
        {
            this.regions = value;
            return this;
        }

        public PageTemplate build()
        {
            return new PageTemplate( this );
        }
    }

    public static class PageTemplateProperties
    {
        ResourcePath parentPath = ResourcePath.root();

        PageTemplateKey key;

        ModuleName moduleName;

        PageTemplateName name;

        String displayName;

        PageDescriptorKey descriptor;

        RootDataSet config;

        ContentTypeNames canRender;

        PageRegions regions;
    }

    public static PageTemplateEditBuilder editPageTemplate( final PageTemplate toBeEdited )
    {
        return new PageTemplateEditBuilder( toBeEdited );
    }

    public static class PageTemplateEditBuilder
        extends PageTemplateProperties
        implements EditBuilder<PageTemplate>
    {

        private final PageTemplate original;

        private final Changes.Builder changes = new Changes.Builder();

        private PageTemplateEditBuilder( PageTemplate original )
        {
            this.original = original;
        }

        public PageTemplateEditBuilder displayName( final String value )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( value ).build() );
            this.displayName = value;
            return this;
        }

        public PageTemplateEditBuilder descriptor( final PageDescriptorKey value )
        {
            changes.recordChange( newPossibleChange( "descriptor" ).from( this.original.getDescriptor() ).to( value ).build() );
            this.descriptor = value;
            return this;
        }

        public PageTemplateEditBuilder config( final RootDataSet value )
        {
            changes.recordChange( newPossibleChange( "config" ).from( this.original.getConfig() ).to( value ).build() );
            this.config = value;
            return this;
        }

        public PageTemplateEditBuilder canRender( final ContentTypeNames value )
        {
            changes.recordChange( newPossibleChange( "canRender" ).from( this.original.getCanRender() ).to( value ).build() );
            this.canRender = value;
            return this;
        }

        public PageTemplateEditBuilder regions( final PageRegions value )
        {
            changes.recordChange( newPossibleChange( "regions" ).from( this.original.getRegions() ).to( value ).build() );
            this.regions = value;
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

        public PageTemplate build()
        {
            return new PageTemplate( this );
        }
    }
}
