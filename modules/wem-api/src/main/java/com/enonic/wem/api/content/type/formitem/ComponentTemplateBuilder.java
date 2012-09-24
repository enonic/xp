package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.Module;

public class ComponentTemplateBuilder
{
    private Component component;

    private Module module;

    public ComponentTemplateBuilder component( Component value )
    {
        this.component = value;
        return this;
    }

    public ComponentTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public ComponentTemplate build()
    {
        Preconditions.checkNotNull( component, "component is required" );

        ComponentTemplate componentTemplate = new ComponentTemplate();
        componentTemplate.setComponent( component );
        componentTemplate.setModule( module );
        return componentTemplate;
    }

    public static ComponentTemplateBuilder newComponentTemplate()
    {
        return new ComponentTemplateBuilder();
    }
}
