package com.enonic.wem.core.content.type.configitem;

import org.elasticsearch.common.base.Preconditions;

import com.enonic.wem.core.module.Module;

public class FieldSetTemplate
    implements Template
{
    private String name;

    private Module module;

    private ConfigItems configItems = new ConfigItems();

    FieldSetTemplate()
    {
    }

    public String getName()
    {
        return name;
    }

    void setName( final String name )
    {
        this.name = name;
    }

    public Module getModule()
    {
        return module;
    }

    void setModule( final Module module )
    {
        this.module = module;
    }

    @Override
    public TemplateType getType()
    {
        return TemplateType.FIELD_SET;
    }

    public TemplateQualifiedName getTemplateQualifiedName()
    {
        return new TemplateQualifiedName( module.getName(), name );
    }

    public void addConfigItem( final ConfigItem configItem )
    {
        //if ( configItem.getConfigItemType() == ConfigItemType.REFERENCE )
        if ( false )
        {
            TemplateReference templateReference = (TemplateReference) configItem;
            Preconditions.checkArgument( templateReference.getTemplateType() == TemplateType.FIELD,
                                         "A template cannot reference other templates unless it's of type %s: " +
                                             templateReference.getTemplateType(), TemplateType.FIELD );
        }
        configItems.addConfigItem( configItem );
    }

    public void addField( final Field field )
    {
        configItems.addConfigItem( field );
    }

    public void addTemplateReference( final TemplateReference templateReference )
    {
        addConfigItem( templateReference );
    }

    public ConfigItem create( final TemplateReference templateReference )
    {
        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( templateReference.getName() ).build();
        fieldSet.setPath( templateReference.getPath() );

        for ( ConfigItem configItem : configItems )
        {
            fieldSet.addConfigItem( configItem.copy() );
        }
        return fieldSet;
    }
}
