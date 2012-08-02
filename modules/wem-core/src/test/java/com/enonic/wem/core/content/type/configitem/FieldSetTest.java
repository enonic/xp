package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class FieldSetTest
{
    @Test
    public void copy()
    {
        // setup
        FieldSet original = FieldSet.newBuilder().typeGroup().name( "name" ).label( "Label" ).multiple( true ).build();
        original.addField( Field.newBuilder().name( "myField" ).type( FieldTypes.textline ).build() );

        // exercise
        FieldSet copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "name", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertNotSame( original.getConfigItems(), copy.getConfigItems() );
        assertNotSame( original.getConfig( new ConfigItemPath( "myField" ) ), copy.getConfig( new ConfigItemPath( "myField" ) ) );
    }

    @Test
    public void getConfig()
    {
        // setup
        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "myFieldSet" ).label( "Label" ).multiple( true ).build();
        fieldSet.addField( Field.newBuilder().name( "myField" ).type( FieldTypes.textline ).build() );

        // exercise
        ConfigItem field = fieldSet.getConfig( new ConfigItemPath( "myField" ) );

        // verify
        assertEquals( "myFieldSet.myField", field.getPath().toString() );
    }
}
