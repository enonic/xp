package com.enonic.wem.core.content.type.formitem;


import org.junit.Test;

import com.enonic.wem.core.content.type.formitem.comptype.ComponentTypes;

import static org.junit.Assert.*;

public class FormItemSetTest
{
    @Test
    public void copy()
    {
        // setup
        FormItemSet original = FormItemSet.newBuilder().name( "name" ).label( "Label" ).multiple( true ).build();
        original.addItem( Component.newBuilder().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build() );

        // exercise
        FormItemSet copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "name", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertNotSame( original.getFormItems(), copy.getFormItems() );
        assertNotSame( original.getFormItem( new FormItemPath( "myField" ) ), copy.getFormItem( new FormItemPath( "myField" ) ) );
    }

    @Test
    public void getConfig()
    {
        // setup
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "myFieldSet" ).label( "Label" ).multiple( true ).build();
        formItemSet.addItem( Component.newBuilder().name( "myField" ).type( ComponentTypes.TEXT_LINE ).build() );

        // exercise
        HierarchicalFormItem field = formItemSet.getFormItem( new FormItemPath( "myField" ) );

        // verify
        assertEquals( "myFieldSet.myField", field.getPath().toString() );
    }

    @Test
    public void setPath()
    {
        FormItemSet formItemSet = FormItemSet.newBuilder().name( "address" ).label( "Address" ).build();
        formItemSet.addItem( Component.newBuilder().name( "street" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "postalCode" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "postalPlace" ).type( ComponentTypes.TEXT_LINE ).build() );
        formItemSet.addItem( Component.newBuilder().name( "country" ).type( ComponentTypes.TEXT_LINE ).build() );

        // exercise & verify
        formItemSet.setName( "homeAddress" );
        formItemSet.setPath( new FormItemPath( "homeAddress" ) );

        // verify
        assertEquals( "homeAddress.street", formItemSet.getFormItem( new FormItemPath( "street" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalCode", formItemSet.getFormItem( new FormItemPath( "postalCode" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalPlace", formItemSet.getFormItem( new FormItemPath( "postalPlace" ) ).getPath().toString() );
        assertEquals( "homeAddress.country", formItemSet.getFormItem( new FormItemPath( "country" ) ).getPath().toString() );
    }
}
