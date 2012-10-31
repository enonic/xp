package com.enonic.wem.api.content.type.component;


import org.junit.Test;

import com.enonic.wem.api.content.type.component.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.component.Input.newInput;
import static org.junit.Assert.*;

public class ComponentSetTest
{
    @Test
    public void copy()
    {
        // setup
        ComponentSet original = ComponentSet.newBuilder().name( "name" ).label( "Label" ).multiple( true ).build();
        original.addInput( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise
        ComponentSet copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "name", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertNotSame( original.getComponents(), copy.getComponents() );
        assertNotSame( original.getHierarchicalComponent( new ComponentPath( "myField" ) ),
                       copy.getHierarchicalComponent( new ComponentPath( "myField" ) ) );
    }

    @Test
    public void getConfig()
    {
        // setup
        ComponentSet componentSet = ComponentSet.newBuilder().name( "myFieldSet" ).label( "Label" ).multiple( true ).build();
        componentSet.addInput( newInput().name( "myField" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise
        HierarchicalComponent field = componentSet.getHierarchicalComponent( new ComponentPath( "myField" ) );

        // verify
        assertEquals( "myFieldSet.myField", field.getPath().toString() );
    }

    @Test
    public void setPath()
    {
        ComponentSet componentSet = ComponentSet.newBuilder().name( "address" ).label( "Address" ).build();
        componentSet.addInput( newInput().name( "street" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "postalCode" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "postalPlace" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "country" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        componentSet.setName( "homeAddress" );
        componentSet.setPath( new ComponentPath( "homeAddress" ) );

        // verify
        assertEquals( "homeAddress.street", componentSet.getHierarchicalComponent( new ComponentPath( "street" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalCode",
                      componentSet.getHierarchicalComponent( new ComponentPath( "postalCode" ) ).getPath().toString() );
        assertEquals( "homeAddress.postalPlace",
                      componentSet.getHierarchicalComponent( new ComponentPath( "postalPlace" ) ).getPath().toString() );
        assertEquals( "homeAddress.country", componentSet.getHierarchicalComponent( new ComponentPath( "country" ) ).getPath().toString() );
    }
}
