package com.enonic.wem.api.content.type.formitem;


import com.enonic.wem.api.module.Module;

public class FormItemSetSubTypeBuilder
{
    private Module module;

    private FormItemSet formItemSet;

    public FormItemSetSubTypeBuilder module( Module value )
    {
        this.module = value;
        return this;
    }

    public FormItemSetSubTypeBuilder formItemSet( FormItemSet value )
    {
        this.formItemSet = value;
        return this;
    }

    public FormItemSetSubType build()
    {
        FormItemSetSubType subType = new FormItemSetSubType();
        subType.setModule( module );
        subType.setFormItemSet( formItemSet );

        return subType;
    }

    public static FormItemSetSubTypeBuilder newFormItemSetSubType()
    {
        return new FormItemSetSubTypeBuilder();
    }
}
