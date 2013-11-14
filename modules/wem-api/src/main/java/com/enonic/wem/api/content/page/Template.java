package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public abstract class Template<NAME extends TemplateName>
{
    private final NAME name;

    private final String displayName;

    private final ModuleResourceKey descriptor;

    private final RootDataSet config;

    protected Template( final NAME name, final String displayName, final ModuleResourceKey descriptor, final RootDataSet config )
    {
        this.name = name;
        this.displayName = displayName;
        this.descriptor = descriptor;
        this.config = config;
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

    public abstract static class BaseTemplateBuilder<T extends BaseTemplateBuilder, NAME extends TemplateName>
    {
        protected NAME name;

        protected String displayName;

        protected ModuleResourceKey descriptor;

        protected RootDataSet config;

        public T name( final NAME name )
        {
            this.name = name;
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
