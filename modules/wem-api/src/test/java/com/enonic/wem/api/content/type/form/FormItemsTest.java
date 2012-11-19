package com.enonic.wem.api.content.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.form.ComponentSetSubType.newComponentSetSubType;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.SubTypeReference.newSubTypeReference;
import static org.junit.Assert.*;

public class FormItemsTest
{
    @Test
    public void getConfig()
    {
        FormItems formItems = new FormItems();
        FormItemSet componentSet = newFormItemSet().name( "personalia" ).build();
        formItems.add( componentSet );
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalFormItem personaliaConfig = formItems.getFormItemSet( new FormItemPath( "personalia" ) );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        FormItems formItems = new FormItems();
        FormItemSet componentSet = newFormItemSet().name( "personalia" ).label( "Personalia" ).build();
        formItems.add( componentSet );
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalFormItem personaliaEyeColourConfig = componentSet.getInput( "eyeColour" );
        assertEquals( "personalia.eyeColour", personaliaEyeColourConfig.getPath().toString() );
    }

    @Test
    public void toString_with_two_fields()
    {
        FormItems formItems = new FormItems();
        formItems.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        formItems.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        assertEquals( "eyeColour, hairColour", formItems.toString() );
    }

    @Test
    public void toString_with_layout()
    {
        FormItems formItems = new FormItems();
        formItems.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        formItems.add( FieldSet.newFieldSet().label( "Layout" ).name( "layout" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() ).build() );

        // exercise & verify
        assertEquals( "name, layout{eyeColour, hairColour}", formItems.toString() );
    }

    @Test
    public void given_sub_type_with_a_input_inside_a_set_when_getComponent_with_path_to_input_then_exception_is_thrown()
    {
        // setup
        Module module = Module.newModule().name( "myModule" ).build();

        Input myInput = newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build();
        FormItemSet mySet = newFormItemSet().name( "mySet" ).add( myInput ).build();
        ComponentSetSubType mySubType = newComponentSetSubType().module( module ).componentSet( mySet ).build();

        FormItems formItems = new FormItems();
        formItems.add( newSubTypeReference().name( "mySet" ).typeInput().subType( mySubType.getQualifiedName() ).build() );

        // exercise & verify
        try
        {
            formItems.getFormItem( new FormItemPath( "mySet.myInput" ) );
        }
        catch ( Exception e )
        {
            assertTrue( "Expected IllegalArgumentException", e instanceof IllegalArgumentException );
            assertEquals(
                "Cannot get formItem [mySet.myInput] because it's past a SubTypeReference [mySet], resolve the SubTypeReference first.",
                e.getMessage() );
        }
    }
}
