package com.enonic.wem.api.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.ComponentSubTypeBuilder.newComponentSubType;
import static com.enonic.wem.api.content.type.formitem.Input.newInput;


public class ComponentSubTypeTest
{
    private Module module = Module.newModule().name( "MyModule" ).build();

    @Test
    public void tags()
    {
        Input input = newInput().name( "tags" ).label( "Tags" ).type( ComponentTypes.TEXT_LINE ).multiple( true ).build();
        ComponentSubType componentSubType = newComponentSubType().module( module ).input( input ).build();
    }
}
