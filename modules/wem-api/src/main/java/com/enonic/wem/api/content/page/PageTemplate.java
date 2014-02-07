package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.schema.content.ContentTypeNames;

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

    private PageTemplate( final Builder builder )
    {
        this.key = resolveKey( builder );
        this.parentPath = builder.parentPath;
        this.path = ResourcePath.from( this.parentPath, this.key.getTemplateName().toString() );
        this.displayName = builder.displayName;
        this.descriptor = builder.descriptor;
        this.config = builder.config;
        this.canRender = builder.canRender != null ? builder.canRender : ContentTypeNames.empty();
        this.regions = builder.regions;
    }

    private PageTemplateKey resolveKey( final Builder properties )
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

    public static PageTemplate.Builder copyOf( final PageTemplate pageTemplate )
    {
        return new Builder( pageTemplate );
    }

    public static class Builder
    {
        private ResourcePath parentPath = ResourcePath.root();

        private PageTemplateKey key;

        private ModuleName moduleName;

        private PageTemplateName name;

        private String displayName;

        private PageDescriptorKey descriptor;

        private RootDataSet config;

        private ContentTypeNames canRender;

        private PageRegions regions;

        private Builder()
        {
        }

        private Builder( final PageTemplate source )
        {
            this.parentPath = source.parentPath;
            this.key = source.key;
            this.displayName = source.displayName;
            this.descriptor = source.descriptor;
            this.config = source.config == null ? null : source.config.copy().toRootDataSet();
            this.canRender = source.canRender;
            this.regions = source.regions;
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

}
