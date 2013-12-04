package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;

public abstract class Template<NAME extends TemplateName, KEY extends TemplateKey<NAME>>
{
    private final ResourcePath parentPath;

    private final ResourcePath path;

    private final KEY key;

    private final NAME name;

    private final String displayName;

    private final ModuleResourceKey descriptor;

    private final RootDataSet config;

    protected Template( final TemplateProperties properties )
    {
        this.parentPath = properties.parentPath;
        this.key = (KEY) properties.key;
        this.name = this.key != null ? this.key.getTemplateName() : (NAME) properties.name;
        this.path = ResourcePath.from( this.parentPath, this.name.toString() );
        this.displayName = properties.displayName;
        this.descriptor = properties.descriptor;
        this.config = properties.config;
    }

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
        return name;
    }

    public ModuleResourceKey getDescriptor()
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

    public abstract static class TemplateProperties<T extends BaseTemplateBuilder, NAME extends TemplateName, KEY extends TemplateKey<NAME>>
    {
        protected ResourcePath parentPath = ResourcePath.root();

        protected KEY key;

        protected NAME name;

        protected String displayName;

        protected ModuleResourceKey descriptor;

        protected RootDataSet config;
    }

    public abstract static class BaseTemplateBuilder<B extends BaseTemplateBuilder, T extends Template, NAME extends TemplateName, KEY extends TemplateKey<NAME>>
        extends TemplateProperties<B, NAME, KEY>
    {
        public B key( final KEY key )
        {
            this.key = key;
            return typecastToTemplateBuilder( this );
        }

        public B name( final NAME name )
        {
            this.name = name;
            return typecastToTemplateBuilder( this );
        }

        public NAME getName()
        {
            return this.name;
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

        public B descriptor( final ModuleResourceKey descriptor )
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
