package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.rendering.Component;

public abstract class BasePageComponent<TEMPLATE_KEY extends TemplateKey>
    implements Component
{
    private String id;

    private final TEMPLATE_KEY template;

    protected BasePageComponent( final Properties<TEMPLATE_KEY> properties )
    {
        Preconditions.checkNotNull( properties.template, "template cannot be null" );
        this.template = properties.template;
    }

    public TEMPLATE_KEY getTemplate()
    {
        return template;
    }

    public DataSet toDataSet()
    {
        final DataSet dataSet = new DataSet( this.getClass().getSimpleName() );
        dataSet.setProperty( "template", new Value.String( this.template.toString() ) );
        return dataSet;
    }

    public static class Properties<TEMPLATE_KEY extends TemplateKey>
    {
        protected TEMPLATE_KEY template;
    }

    public static class Builder<TEMPLATE_KEY extends TemplateKey>
        extends Properties<TEMPLATE_KEY>
    {
        public Builder<TEMPLATE_KEY> template( TEMPLATE_KEY value )
        {
            this.template = value;
            return this;
        }
    }
}
