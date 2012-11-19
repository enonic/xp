package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.form.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class ComponentSetTest
{
    @Test
    public void copy()
    {
        // setup
        ComponentSet original = newComponentSet().name( "name" ).label( "Label" ).multiple( true ).build();
        original.add( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise
        ComponentSet copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "name", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertNotSame( original.getComponents(), copy.getComponents() );
        assertNotSame( original.getInput( new ComponentPath( "myField" ) ), copy.getInput( new ComponentPath( "myField" ) ) );
    }

    @Test
    public void getConfig()
    {
        // setup
        ComponentSet componentSet = newComponentSet().name( "myFieldSet" ).label( "Label" ).multiple( true ).build();
        componentSet.add( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise
        HierarchicalComponent field = componentSet.getInput( new ComponentPath( "myField" ) );

        // verify
        assertEquals( "myFieldSet.myField", field.getPath().toString() );
    }

    @Test
    public void setPath()
    {
        ComponentSet componentSet = newComponentSet().name( "address" ).label( "Address" ).build();
        componentSet.add( newInput().name( "street" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "postalCode" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "postalPlace" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "country" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        componentSet.setName( "homeAddress" );
        componentSet.setPath( new ComponentPath( "homeAddress" ) );

        // verify
        assertEquals( "homeAddress.street", componentSet.getInput( new ComponentPath( "street" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalCode", componentSet.getInput( new ComponentPath( "postalCode" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalPlace", componentSet.getInput( new ComponentPath( "postalPlace" ) ).getPath().toString() );
        assertEquals( "homeAddress.country", componentSet.getInput( new ComponentPath( "country" ) ).getPath().toString() );
    }
}
