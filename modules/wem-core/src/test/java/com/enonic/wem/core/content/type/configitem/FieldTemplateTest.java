package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.Field.newField;
import static com.enonic.wem.core.content.type.configitem.FieldTemplateBuilder.newFieldTemplate;
import static com.enonic.wem.core.module.Module.newModule;


public class FieldTemplateTest
{
    private Module module = newModule().name( "MyModule" ).build();

    @Test
    public void tags()
    {
        Field field = newField().name( "tags" ).label( "Tags" ).type( FieldTypes.TEXT_LINE ).multiple( true ).build();
        FieldTemplate fieldTemplate = newFieldTemplate().module( module ).field( field ).build();
    }
}
