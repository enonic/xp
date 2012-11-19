package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class ComponentSetTest
{
    @Test
    public void copy()
    {
        // setup
        FormItemSet original = newFormItemSet().name( "name" ).label( "Label" ).multiple( true ).build();
        original.add( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise
        FormItemSet copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "name", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertNotSame( original.getFormItems(), copy.getFormItems() );
        assertNotSame( original.getInput( new FormItemPath( "myField" ) ), copy.getInput( new FormItemPath( "myField" ) ) );
    }

    @Test
    public void getConfig()
    {
        // setup
        FormItemSet componentSet = newFormItemSet().name( "myFieldSet" ).label( "Label" ).multiple( true ).build();
        componentSet.add( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise
        HierarchicalFormItem field = componentSet.getInput( new FormItemPath( "myField" ) );

        // verify
        assertEquals( "myFieldSet.myField", field.getPath().toString() );
    }

    @Test
    public void setPath()
    {
        FormItemSet componentSet = newFormItemSet().name( "address" ).label( "Address" ).build();
        componentSet.add( newInput().name( "street" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "postalCode" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "postalPlace" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "country" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        componentSet.setName( "homeAddress" );
        componentSet.setPath( new FormItemPath( "homeAddress" ) );

        // verify
        assertEquals( "homeAddress.street", componentSet.getInput( new FormItemPath( "street" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalCode", componentSet.getInput( new FormItemPath( "postalCode" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalPlace", componentSet.getInput( new FormItemPath( "postalPlace" ) ).getPath().toString() );
        assertEquals( "homeAddress.country", componentSet.getInput( new FormItemPath( "country" ) ).getPath().toString() );
    }
}
