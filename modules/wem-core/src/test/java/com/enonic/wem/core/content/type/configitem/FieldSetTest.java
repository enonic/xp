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

    @Test
    public void setPath()
    {
        FieldSet fieldSet = FieldSet.newBuilder().typeGroup().name( "address" ).label( "Address" ).build();
        fieldSet.addField( Field.newBuilder().name( "street" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "postalCode" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "postalPlace" ).type( FieldTypes.textline ).build() );
        fieldSet.addField( Field.newBuilder().name( "country" ).type( FieldTypes.textline ).build() );

        // exercise & verify
        fieldSet.setName( "homeAddress" );
        fieldSet.setPath( new ConfigItemPath( "homeAddress" ) );

        // verify
        assertEquals( "homeAddress.street", fieldSet.getConfig( new ConfigItemPath( "street" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalCode", fieldSet.getConfig( new ConfigItemPath( "postalCode" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalPlace", fieldSet.getConfig( new ConfigItemPath( "postalPlace" ) ).getPath().toString() );
        assertEquals( "homeAddress.country", fieldSet.getConfig( new ConfigItemPath( "country" ) ).getPath().toString() );
    }
}
