package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;

public abstract class Template<ID extends TemplateId, NAME extends TemplateName>
{
    private final ID id;

    private final NAME name;

    private final String displayName;

    private final ModuleResourceKey descriptor;

    private final RootDataSet config;

    protected Template( final NAME name, final ID id, final String displayName, final ModuleResourceKey descriptor,
                        final RootDataSet config )
    {
        this.name = name;
        this.id = id;
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

    protected abstract static class BaseTemplateBuilder<T extends BaseTemplateBuilder, ID extends TemplateId, NAME extends TemplateName>
    {
        protected NAME name;

        protected ID id;

        protected String displayName;

        protected ModuleResourceKey descriptor;

        protected RootDataSet config;

        public T name( final NAME name )
        {
            this.name = name;
            return (T) this;
        }

        public T id( final ID id )
        {
            this.id = id;
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
