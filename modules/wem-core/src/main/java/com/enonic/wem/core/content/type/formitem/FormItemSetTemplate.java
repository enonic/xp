package com.enonic.wem.core.content.type.formitem;

import org.elasticsearch.common.base.Preconditions;

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
    public TemplateType getType()
    {
        return TemplateType.FORM_ITEM_SET;
    }

    public void addFormItem( final HierarchicalFormItem formItem )
    {
        if ( formItem instanceof TemplateReference )
        {
            TemplateReference templateReference = (TemplateReference) formItem;
            Preconditions.checkArgument( templateReference.getTemplateType() == TemplateType.COMPONENT,
                                         "A template cannot reference other templates unless it is of type %s: " +
                                             templateReference.getTemplateType(), TemplateType.COMPONENT );
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
