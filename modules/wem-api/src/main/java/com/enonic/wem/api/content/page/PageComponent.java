package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.rendering.Component;

public abstract class PageComponent<TEMPLATE_KEY extends TemplateKey>
    implements Component
{
    private ComponentName name;

    private final TEMPLATE_KEY template;

    private final RootDataSet config;

    protected PageComponent( final Properties<TEMPLATE_KEY> properties )
    {
        Preconditions.checkNotNull( properties.name, "name cannot be null" );
        Preconditions.checkNotNull( properties.template, "template cannot be null" );
        Preconditions.checkNotNull( properties.config, "config cannot be null" );
        this.template = properties.template;
        this.name = properties.name;
        this.config = properties.config;
    }

    public ComponentName getName()
    {
        return name;
    }

    public TEMPLATE_KEY getTemplate()
    {
        return template;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public static class Properties<TEMPLATE_KEY extends TemplateKey>
    {
        protected ComponentName name;

        protected TEMPLATE_KEY template;

        protected RootDataSet config;
    }

    public static class Builder<TEMPLATE_KEY extends TemplateKey>
        extends Properties<TEMPLATE_KEY>
    {
        protected Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder name( ComponentName value )
        {
            this.name = value;
            return this;
        }

        public Builder<TEMPLATE_KEY> template( TEMPLATE_KEY value )
        {
            this.template = value;
            return this;
        }

        public Builder config( final RootDataSet config )
        {
            this.config = config;
            return this;
        }
    }
}
