package com.enonic.wem.core.content.type.configitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

import static org.junit.Assert.*;

public class FormItemSetTest
{
    @Test
    public void copy()
    {
        // setup
        FormItemSet original = FormItemSet.newBuilder().name( "name" ).label( "Label" ).multiple( true ).build();
        original.addField( Component.newBuilder().name( "myField" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise
        FormItemSet copy = original.copy();

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
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "myFieldSet" ).label( "Label" ).multiple( true ).build();
        formItemSet.addField( Component.newBuilder().name( "myField" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise
        DirectAccessibleFormItem field = formItemSet.getConfigItem( new ConfigItemPath( "myField" ) );

        // verify
        assertEquals( "myFieldSet.myField", field.getPath().toString() );
    }

    @Test
    public void setPath()
    {
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "address" ).label( "Address" ).build();
        formItemSet.addField( Component.newBuilder().name( "street" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addField( Component.newBuilder().name( "postalCode" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addField( Component.newBuilder().name( "postalPlace" ).type( FieldTypes.TEXT_LINE ).build() );
        formItemSet.addField( Component.newBuilder().name( "country" ).type( FieldTypes.TEXT_LINE ).build() );

        // exercise & verify
        formItemSet.setName( "homeAddress" );
        formItemSet.setPath( new ConfigItemPath( "homeAddress" ) );

        // verify
        assertEquals( "homeAddress.street", formItemSet.getConfigItem( new ConfigItemPath( "street" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalCode", formItemSet.getConfigItem( new ConfigItemPath( "postalCode" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalPlace", formItemSet.getConfigItem( new ConfigItemPath( "postalPlace" ) ).getPath().toString() );
        assertEquals( "homeAddress.country", formItemSet.getConfigItem( new ConfigItemPath( "country" ) ).getPath().toString() );
    }
}
