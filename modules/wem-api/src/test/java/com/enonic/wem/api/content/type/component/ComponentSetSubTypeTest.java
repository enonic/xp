package com.enonic.wem.api.content.type.component;

import org.junit.Test;

import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.ComponentSetSubType.newComponentSetSubType;
import static com.enonic.wem.api.content.type.component.Input.newInput;
import static com.enonic.wem.api.content.type.component.InputSubType.newInputSubType;
import static com.enonic.wem.api.content.type.component.SubTypeReference.newSubTypeReference;
import static org.junit.Assert.*;

public class ComponentSetSubTypeTest
{

    @Test
    public void adding_a_fieldSetSubType_to_another_fieldSetSubType_throws_exception()
    {
        Module module = Module.newModule().name( "myModule" ).build();

        InputSubType ageSubType =
            newInputSubType().module( module ).input( newInput().name( "age" ).type( InputTypes.TEXT_LINE ).build() ).build();

        ComponentSetSubType personSubType = newComponentSetSubType().module( module ).componentSet(
            newComponentSet().name( "person" ).add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newSubTypeReference( ageSubType ).name( "age" ).build() ).build() ).build();

        ComponentSetSubType addressSubType = newComponentSetSubType().module( module ).componentSet(
            newComponentSet().name( "address" ).add( newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).build() ).build();

        try
        {
            personSubType.addComponent( newSubTypeReference( addressSubType ).name( "address" ).build() );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "A SubType cannot reference other SubTypes unless it is of type InputSubType: ComponentSetSubType",
                          e.getMessage() );
        }
    }

}
