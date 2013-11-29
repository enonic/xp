package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;

public abstract class Template<NAME extends TemplateName>
{
    private final ResourcePath parentPath;

    private final ResourcePath path;

    private final NAME name;

    private final String displayName;

    private final ModuleResourceKey descriptor;

    private final RootDataSet config;

    protected Template( final TemplateProperties properties )
    {
        this.parentPath = properties.parentPath;
        this.name = (NAME) properties.name;
        this.path = ResourcePath.from( this.parentPath, this.name.toString() );
        this.displayName = properties.displayName;
        this.descriptor = properties.descriptor;
        this.config = properties.config;
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

    public abstract static class TemplateProperties<T extends BaseTemplateBuilder, NAME extends TemplateName>
    {
        protected ResourcePath parentPath = ResourcePath.root();

        protected NAME name;

        protected String displayName;

        protected ModuleResourceKey descriptor;

        protected RootDataSet config;
    }

    public abstract static class BaseTemplateBuilder<T extends BaseTemplateBuilder, NAME extends TemplateName>
        extends TemplateProperties<T, NAME>
    {
        public T name( final NAME name )
        {
            this.name = name;
            return typecastToTemplateBuilder( this );
        }

        public T parentPath( final ResourcePath parentPath )
        {
            this.parentPath = parentPath;
            return typecastToTemplateBuilder( this );
        }

        public T displayName( final String displayName )
        {
            this.displayName = displayName;
            return typecastToTemplateBuilder( this );
        }

        public T descriptor( final ModuleResourceKey descriptor )
        {
            this.descriptor = descriptor;
            return typecastToTemplateBuilder( this );
        }

        public T config( final RootDataSet config )
        {
            this.config = config;
            return typecastToTemplateBuilder( this );
        }

        @SuppressWarnings("unchecked")
        private T typecastToTemplateBuilder( final BaseTemplateBuilder object )
        {
            return (T) object;
        }
    }
}
