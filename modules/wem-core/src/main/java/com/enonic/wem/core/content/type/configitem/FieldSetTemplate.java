package com.enonic.wem.core.content.type.configitem;

import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.module.Module;

public class FieldSetTemplate
    implements Template
{
    private Module module;

    private FieldSet fieldSet = new FieldSet();

    FieldSetTemplate()
    {
    }

    public String getName()
    {
        return fieldSet.getName();
    }

    public Module getModule()
    {
        return module;
    }

    void setModule( final Module module )
    {
        this.module = module;
    }

    public void setFieldSet( final FieldSet fieldSet )
    {
        this.fieldSet = fieldSet;
    }

    @Override
    public TemplateType getType()
    {
        return TemplateType.FIELD_SET;
    }

    public TemplateQualifiedName getTemplateQualifiedName()
    {
        return new TemplateQualifiedName( module.getName(), getName() );
    }

    public void addConfigItem( final ConfigItem configItem )
    {
        if ( configItem.getConfigItemType() == ConfigItemType.REFERENCE )
        {
            TemplateReference templateReference = (TemplateReference) configItem;
            Preconditions.checkArgument( templateReference.getTemplateType() == TemplateType.FIELD,
                                         "A template cannot reference other templates unless it is of type %s: " +
                                             templateReference.getTemplateType(), TemplateType.FIELD );
        }
        fieldSet.addConfigItem( configItem );
    }

    public ConfigItem create( final TemplateReference templateReference )
    {
        FieldSet fieldSet = this.fieldSet.copy();
        fieldSet.setName( templateReference.getName() );
        fieldSet.setPath( templateReference.getPath() );
        return fieldSet;
    }
}
