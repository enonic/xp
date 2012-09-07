package com.enonic.wem.core.content.type.configitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.core.module.Module;

public class FieldTemplateBuilder
{
    private Component component;

    private Module module;

    public FieldTemplateBuilder field( Component value )
    {
        this.component = value;
        return this;
    }

    public FieldTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public ComponentTemplate build()
    {
        Preconditions.checkNotNull( component, "field is required" );

        ComponentTemplate componentTemplate = new ComponentTemplate();
        componentTemplate.setComponent( component );
        componentTemplate.setModule( module );
        return componentTemplate;
    }

    public static FieldTemplateBuilder newFieldTemplate()
    {
        return new FieldTemplateBuilder();
    }
}
