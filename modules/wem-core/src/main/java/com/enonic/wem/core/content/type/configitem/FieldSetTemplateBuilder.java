package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.module.Module;

public class FieldSetTemplateBuilder
{
    private Module module;

    private FieldSet fieldSet;

    public FieldSetTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public FieldSetTemplateBuilder fieldSet( FieldSet value )
    {
        this.fieldSet = value;
        return this;
    }

    public FieldSetTemplate build()
    {
        FieldSetTemplate fieldSetTemplate = new FieldSetTemplate();
        fieldSetTemplate.setModule( module );
        fieldSetTemplate.setFieldSet( fieldSet );

        return fieldSetTemplate;
    }

    public static FieldSetTemplateBuilder create()
    {
        return new FieldSetTemplateBuilder();
    }

    public static FieldSetTemplateBuilder newFieldSetTemplate()
    {
        return new FieldSetTemplateBuilder();
    }
}
