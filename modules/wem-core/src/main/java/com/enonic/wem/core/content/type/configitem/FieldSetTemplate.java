package com.enonic.wem.core.content.type.configitem;

import org.elasticsearch.common.base.Preconditions;

public class FieldSetTemplate
    extends Template
{
    private FieldSet fieldSet = new FieldSet();

    FieldSetTemplate()
    {
    }

    public String getName()
    {
        return fieldSet.getName();
    }

    void setFieldSet( final FieldSet fieldSet )
    {
        this.fieldSet = fieldSet;
    }

    @Override
    public TemplateType getType()
    {
        return TemplateType.FIELD_SET;
    }

    public void addConfigItem( final DirectAccessibleConfigItem configItem )
    {
        if ( configItem instanceof TemplateReference )
        {
            TemplateReference templateReference = (TemplateReference) configItem;
            Preconditions.checkArgument( templateReference.getTemplateType() == TemplateType.FIELD,
                                         "A template cannot reference other templates unless it is of type %s: " +
                                             templateReference.getTemplateType(), TemplateType.FIELD );
        }
        fieldSet.addConfigItem( configItem );
    }

    public DirectAccessibleConfigItem create( final TemplateReference templateReference )
    {
        FieldSet fieldSet = this.fieldSet.copy();
        fieldSet.setName( templateReference.getName() );
        fieldSet.setPath( templateReference.getPath() );
        return fieldSet;
    }
}
