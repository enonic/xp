package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public abstract class Template<ID extends TemplateId>
{
    private final ID id;

    private final String displayName;

    private final ModuleResourceKey descriptor;

    private final RootDataSet config;

    protected Template( final ID id, final String displayName, final ModuleResourceKey descriptor, final RootDataSet config )
    {
        this.id = id;
        this.displayName = displayName;
        this.descriptor = descriptor;
        this.config = config;
    }

    public ModuleResourceKey getDescriptor()
    {
        return descriptor;
    }

    public ID getId()
    {
        return id;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    protected abstract static class BaseTemplateBuilder<T extends BaseTemplateBuilder, ID extends TemplateId>
    {
        protected ID id;

        protected String displayName;

        protected ModuleResourceKey descriptor;

        protected RootDataSet config;

        public T id( final ID value )
        {
            this.id = value;
            return (T) this;
        }

        public T displayName( final String displayName )
        {
            this.displayName = displayName;
            return (T) this;
        }

        public T descriptor( final ModuleResourceKey descriptor )
        {
            this.descriptor = descriptor;
            return (T) this;
        }

        public T config( final RootDataSet config )
        {
            this.config = config;
            return (T) this;
        }
    }
}
