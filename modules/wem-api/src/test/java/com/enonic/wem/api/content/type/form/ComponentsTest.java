package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.form.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.form.ComponentSetSubType.newComponentSetSubType;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.SubTypeReference.newSubTypeReference;
import static org.junit.Assert.*;

public class ComponentsTest
{
    @Test
    public void getConfig()
    {
        Components components = new Components();
        ComponentSet componentSet = newComponentSet().name( "personalia" ).build();
        components.add( componentSet );
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalComponent personaliaConfig = components.getComponentSet( new ComponentPath( "personalia" ) );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        Components components = new Components();
        ComponentSet componentSet = newComponentSet().name( "personalia" ).label( "Personalia" ).build();
        components.add( componentSet );
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalComponent personaliaEyeColourConfig = componentSet.getInput( "eyeColour" );
        assertEquals( "personalia.eyeColour", personaliaEyeColourConfig.getPath().toString() );
    }

    @Test
    public void toString_with_two_fields()
    {
        Components components = new Components();
        components.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        components.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        assertEquals( "eyeColour, hairColour", components.toString() );
    }

    @Test
    public void toString_with_layout()
    {
        Components components = new Components();
        components.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        components.add( FieldSet.newFieldSet().label( "Layout" ).name( "layout" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() ).build() );

        // exercise & verify
        assertEquals( "name, layout{eyeColour, hairColour}", components.toString() );
    }

    @Test
    public void given_sub_type_with_a_input_inside_a_set_when_getComponent_with_path_to_input_then_exception_is_thrown()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        Input myInput = newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build();
        ComponentSet mySet = newComponentSet().name( "mySet" ).add( myInput ).build();
        ComponentSetSubType mySubType = newComponentSetSubType().module( module ).componentSet( mySet ).build();

        Components components = new Components();
        components.add( newSubTypeReference().name( "mySet" ).typeInput().subType( mySubType.getQualifiedName() ).build() );

        // exercise & verify
        try
        {
            components.getComponent( new ComponentPath( "mySet.myInput" ) );
        }
        catch ( Exception e )
        {
            assertTrue( "Expected IllegalArgumentException", e instanceof IllegalArgumentException );
            assertEquals(
                "Cannot get component [mySet.myInput] because it's past a SubTypeReference [mySet], resolve the SubTypeReference first.",
                e.getMessage() );
        }
    }
}
