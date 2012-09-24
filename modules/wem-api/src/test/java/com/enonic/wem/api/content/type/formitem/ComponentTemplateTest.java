package com.enonic.wem.api.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.Component.newComponent;
import static com.enonic.wem.api.content.type.formitem.ComponentTemplateBuilder.newComponentTemplate;


public class ComponentTemplateTest
{
    private Module module = Module.newModule().name( "MyModule" ).build();

    @Test
    public void tags()
    {
        Component component = newComponent().name( "tags" ).label( "Tags" ).type( ComponentTypes.TEXT_LINE ).multiple( true ).build();
        ComponentTemplate componentTemplate = newComponentTemplate().module( module ).component( component ).build();
    }
}
