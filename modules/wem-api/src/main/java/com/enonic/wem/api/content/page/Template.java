package com.enonic.wem.api.content.page;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ResourcePath;

public abstract class Template<NAME extends TemplateName, KEY extends TemplateKey<NAME>, DESCRIPTOR_KEY extends DescriptorKey>
{
    private final ResourcePath parentPath;

    private final ResourcePath path;

    private final KEY key;

    private final String displayName;

    private final DESCRIPTOR_KEY descriptor;

    private final RootDataSet config;

    protected Template( final TemplateProperties properties )
    {
        this.key = resolveKey( properties );
        this.parentPath = properties.parentPath;
        this.path = ResourcePath.from( this.parentPath, this.key.getTemplateName().toString() );
        this.displayName = properties.displayName;
        this.descriptor = (DESCRIPTOR_KEY) properties.descriptor;
        this.config = properties.config;
    }

    private KEY resolveKey( final TemplateProperties properties )
    {
        if ( properties.key != null )
        {
            return (KEY) properties.key;
        }
        else
        {
            Preconditions.checkNotNull( properties.siteTemplateKey, "siteTemplateKey cannot be null when key is not given" );
            Preconditions.checkNotNull( properties.moduleKey, "moduleKey cannot be null when key is not given" );
            Preconditions.checkNotNull( properties.siteTemplateKey, "name cannot be null when key is not given" );
            return createKey( properties.siteTemplateKey, properties.moduleKey, (NAME) properties.name );
        }
    }

    protected abstract KEY createKey( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey, final NAME name );

    public KEY getKey()
    {
        return key;
    }

    public ResourcePath getParentPath()
    {
        return parentPath;
    }

    public ResourcePath getPath()
    {
        return path;
    }

    public NAME getName()
    {
        return this.key.getTemplateName();
    }

    public DESCRIPTOR_KEY getDescriptor()
    {
        return descriptor;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public abstract static class TemplateProperties<NAME extends TemplateName, KEY extends TemplateKey<NAME>, DESCRIPTOR_KEY extends DescriptorKey>
    {
        protected ResourcePath parentPath = ResourcePath.root();

        protected KEY key;

        protected SiteTemplateKey siteTemplateKey;

        protected ModuleKey moduleKey;

        protected NAME name;

        protected String displayName;

        protected DESCRIPTOR_KEY descriptor;

        protected RootDataSet config;
    }

    public abstract static class BaseTemplateBuilder<B extends BaseTemplateBuilder, T extends Template, NAME extends TemplateName, KEY extends TemplateKey<NAME>, DESCRIPTOR_KEY extends DescriptorKey>
        extends TemplateProperties<NAME, KEY, DESCRIPTOR_KEY>
    {
        public B key( final KEY key )
        {
            this.key = key;
            return typecastToTemplateBuilder( this );
        }

        /**
         * Optional. Only required when key is not given.
         */
        public B siteTemplate( final SiteTemplateKey value )
        {

            this.siteTemplateKey = value;
            return typecastToTemplateBuilder( this );
        }

        /**
         * Optional. Only required when key is not given.
         */
        public B module( final ModuleKey value )
        {

            this.moduleKey = value;
            return typecastToTemplateBuilder( this );
        }

        /**
         * Optional. Only required when key is not given.
         */
        public B name( final NAME name )
        {
            this.name = name;
            return typecastToTemplateBuilder( this );
        }

        public B parentPath( final ResourcePath parentPath )
        {
            this.parentPath = parentPath;
            return typecastToTemplateBuilder( this );
        }

        public B displayName( final String displayName )
        {
            this.displayName = displayName;
            return typecastToTemplateBuilder( this );
        }

        public B descriptor( final DESCRIPTOR_KEY descriptor )
        {
            this.descriptor = descriptor;
            return typecastToTemplateBuilder( this );
        }

        public B config( final RootDataSet config )
        {
            this.config = config;
            return typecastToTemplateBuilder( this );
        }

        public abstract T build();

        @SuppressWarnings("unchecked")
        private B typecastToTemplateBuilder( final BaseTemplateBuilder object )
        {
            return (B) object;
        }
    }
}
