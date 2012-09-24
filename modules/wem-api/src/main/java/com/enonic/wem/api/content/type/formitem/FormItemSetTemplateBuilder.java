package com.enonic.wem.api.content.type.formitem;


import com.enonic.wem.api.module.Module;

public class FormItemSetTemplateBuilder
{
    private Module module;

    private FormItemSet formItemSet;

    public FormItemSetTemplateBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public FormItemSetTemplateBuilder formItemSet( FormItemSet value )
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
