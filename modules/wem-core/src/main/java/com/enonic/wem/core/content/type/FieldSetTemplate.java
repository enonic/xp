package com.enonic.wem.core.content.type;

import com.enonic.wem.core.content.type.configitem.ConfigItem;
import com.enonic.wem.core.content.type.configitem.ConfigItems;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldSet;
import com.enonic.wem.core.content.type.configitem.TemplateReference;
import com.enonic.wem.core.module.Module;

public class FieldSetTemplate
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

    public TemplateQualifiedName getTemplateQualifiedName()
    {
        return new TemplateQualifiedName( module.getName(), name );
    }

    public ConfigItems getConfigItems()
    {
        return configItems;
    }

    public void addField( final Field field )
    {
        configItems.addConfigItem( field );
    }


    public FieldSet createFieldSet( final TemplateReference templateReference )
    {
        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( templateReference.getName() ).build();

        for ( ConfigItem configItem : configItems )
        {
            fieldSet.addConfigItem( configItem.copy() );
        }
        return fieldSet;
    }
}
