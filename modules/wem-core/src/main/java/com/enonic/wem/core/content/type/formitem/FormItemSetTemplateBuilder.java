package com.enonic.wem.core.content.type.formitem;


import com.enonic.wem.core.module.Module;

public class FormItemSetTemplateBuilder
{
    private Module module;

    private FormItemSet formItemSet;

    public FormItemSetTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public FormItemSetTemplateBuilder fieldSet( FormItemSet value )
    {
        this.formItemSet = value;
        return this;
    }

    public FormItemSetTemplate build()
    {
        FormItemSetTemplate formItemSetTemplate = new FormItemSetTemplate();
        formItemSetTemplate.setModule( module );
        formItemSetTemplate.setFormItemSet( formItemSet );

        return formItemSetTemplate;
    }

    public static FormItemSetTemplateBuilder newFormItemSetTemplate()
    {
        return new FormItemSetTemplateBuilder();
    }
}
