package com.enonic.wem.core.content.type.configitem;


import com.enonic.wem.core.module.Module;

public class FieldSetTemplateBuilder
{
    private Module module;

    private FormItemSet formItemSet;

    public FieldSetTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public FieldSetTemplateBuilder fieldSet( FormItemSet value )
    {
        this.formItemSet = value;
        return this;
    }

    public FieldSetTemplate build()
    {
        FieldSetTemplate fieldSetTemplate = new FieldSetTemplate();
        fieldSetTemplate.setModule( module );
        fieldSetTemplate.setFormItemSet( formItemSet );

        return fieldSetTemplate;
    }

    public static FieldSetTemplateBuilder newFieldSetTemplate()
    {
        return new FieldSetTemplateBuilder();
    }
}
