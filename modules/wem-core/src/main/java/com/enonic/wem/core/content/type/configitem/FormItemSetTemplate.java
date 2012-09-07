package com.enonic.wem.core.content.type.configitem;

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
        return TemplateType.FIELD_SET;
    }

    public void addConfigItem( final DirectAccessibleFormItem configItem )
    {
        if ( configItem instanceof TemplateReference )
        {
            TemplateReference templateReference = (TemplateReference) configItem;
            Preconditions.checkArgument( templateReference.getTemplateType() == TemplateType.FIELD,
                                         "A template cannot reference other templates unless it is of type %s: " +
                                             templateReference.getTemplateType(), TemplateType.FIELD );
        }
        formItemSet.addConfigItem( configItem );
    }

    public DirectAccessibleFormItem create( final TemplateReference templateReference )
    {
        FormItemSet formItemSet = this.formItemSet.copy();
        formItemSet.setName( templateReference.getName() );
        formItemSet.setPath( templateReference.getPath() );
        return formItemSet;
    }
}
