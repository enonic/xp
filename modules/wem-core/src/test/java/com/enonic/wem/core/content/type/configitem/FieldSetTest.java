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
        FieldSet original = FieldSet.newBuilder().name( "name" ).label( "Label" ).multiple( true ).build();
        original.addField( Component.newBuilder().name( "myField" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise
        FieldSet copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "name", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertNotSame( original.getConfigItems(), copy.getConfigItems() );
        assertNotSame( original.getConfigItem( new ConfigItemPath( "myField" ) ), copy.getConfigItem( new ConfigItemPath( "myField" ) ) );
    }

    @Test
    public void getConfig()
    {
        // setup
        FieldSet fieldSet = FieldSet.newBuilder().name( "myFieldSet" ).label( "Label" ).multiple( true ).build();
        fieldSet.addField( Component.newBuilder().name( "myField" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise
        DirectAccessibleConfigItem field = fieldSet.getConfigItem( new ConfigItemPath( "myField" ) );

        // verify
        assertEquals( "myFieldSet.myField", field.getPath().toString() );
    }

    @Test
    public void setPath()
    {
        FieldSet fieldSet = FieldSet.newBuilder().name( "address" ).label( "Address" ).build();
        fieldSet.addField( Component.newBuilder().name( "street" ).type( FieldTypes.TEXT_LINE ).build() );
        fieldSet.addField( Component.newBuilder().name( "postalCode" ).type( FieldTypes.TEXT_LINE ).build() );
        fieldSet.addField( Component.newBuilder().name( "postalPlace" ).type( FieldTypes.TEXT_LINE ).build() );
        fieldSet.addField( Component.newBuilder().name( "country" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise & verify
        fieldSet.setName( "homeAddress" );
        fieldSet.setPath( new ConfigItemPath( "homeAddress" ) );

        // verify
        assertEquals( "homeAddress.street", fieldSet.getConfigItem( new ConfigItemPath( "street" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalCode", fieldSet.getConfigItem( new ConfigItemPath( "postalCode" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalPlace", fieldSet.getConfigItem( new ConfigItemPath( "postalPlace" ) ).getPath().toString() );
        assertEquals( "homeAddress.country", fieldSet.getConfigItem( new ConfigItemPath( "country" ) ).getPath().toString() );
    }
}
