package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.module.Module;

public class FieldSetTemplateBuilder
{
    private String name;

    private Module module;

    public FieldSetTemplateBuilder name( String value )
    {
        this.name = value;
        return this;
    }

    public FieldSetTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public FieldSetTemplate build()
    {
        FieldSetTemplate fieldSetTemplate = new FieldSetTemplate();
        fieldSetTemplate.setName( name );
        fieldSetTemplate.setModule( module );
        return fieldSetTemplate;
    }

    public static FieldSetTemplateBuilder create()
    {
        return new FieldSetTemplateBuilder();
    }
}
