package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Component.newField;
import static com.enonic.wem.core.content.type.configitem.FieldTemplateBuilder.newFieldTemplate;
import static com.enonic.wem.core.module.Module.newModule;


public class ComponentTemplateTest
{
    private Module module = newModule().name( "MyModule" ).build();

    @Test
    public void tags()
    {
        Component component = newField().name( "tags" ).label( "Tags" ).type( FieldTypes.TEXT_LINE ).multiple( true ).build();
        ComponentTemplate componentTemplate = newFieldTemplate().module( module ).field( component ).build();
    }
}
