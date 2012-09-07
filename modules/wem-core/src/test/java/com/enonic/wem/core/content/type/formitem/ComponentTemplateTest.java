package com.enonic.wem.core.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.formitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.formitem.Component.newComponent;
import static com.enonic.wem.core.content.type.formitem.ComponentTemplateBuilder.newComponentTemplate;
import static com.enonic.wem.core.module.Module.newModule;


public class ComponentTemplateTest
{
    private Module module = newModule().name( "MyModule" ).build();

    @Test
    public void tags()
    {
        Component component = newComponent().name( "tags" ).label( "Tags" ).type( FieldTypes.TEXT_LINE ).multiple( true ).build();
        ComponentTemplate componentTemplate = newComponentTemplate().module( module ).component( component ).build();
    }
}
