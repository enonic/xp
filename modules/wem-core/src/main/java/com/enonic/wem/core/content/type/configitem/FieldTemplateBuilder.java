package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.module.Module;

public class FieldTemplateBuilder
{
    private String name;

    private Module module;

    public FieldTemplateBuilder name( String value )
    {
        this.name = value;
        return this;
    }

    public FieldTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public FieldTemplate build()
    {
        FieldTemplate fieldTemplate = new FieldTemplate();
        fieldTemplate.setName( name );
        fieldTemplate.setModule( module );
        return fieldTemplate;
    }

    public static FieldTemplateBuilder create()
    {
        return new FieldTemplateBuilder();
    }
}
