package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

public class FormItemSetTemplate
    extends Template
{
    private FormItemSet formItemSet = new FormItemSet();

    FormItemSetTemplate()
    {
    }

    public String getName()
    {
        return formItemSet.getName();
    }

    void setFormItemSet( final FormItemSet formItemSet )
    {
        this.formItemSet = formItemSet;
    }

    @Override
    public Class getType()
    {
        return this.getClass();
    }

    public void addFormItem( final HierarchicalFormItem formItem )
    {
        if ( formItem instanceof TemplateReference )
        {
            TemplateReference templateReference = (TemplateReference) formItem;
            Preconditions.checkArgument( templateReference.getTemplateType().equals( ComponentTemplate.class ),
                                         "A template cannot reference other templates unless it is of type %s: " +
                                             templateReference.getTemplateType().getSimpleName(), ComponentTemplate.class.getSimpleName() );
        }
        formItemSet.addFormItem( formItem );
    }

    public HierarchicalFormItem create( final TemplateReference templateReference )
    {
        FormItemSet formItemSet = this.formItemSet.copy();
        formItemSet.setName( templateReference.getName() );
        formItemSet.setPath( templateReference.getPath() );
        return formItemSet;
    }
}
