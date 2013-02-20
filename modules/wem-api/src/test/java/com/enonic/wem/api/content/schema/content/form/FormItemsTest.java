package com.enonic.wem.api.content.schema.content.form;


import org.junit.Test;

import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.schema.content.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static com.enonic.wem.api.content.schema.content.form.MixinReference.newMixinReference;
import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;
import static org.junit.Assert.*;

public class FormItemsTest
{
    @Test
    public void getConfig()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).build();
        formItems.add( formItemSet );
        formItemSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalFormItem personaliaConfig = formItems.getFormItemSet( new FormItemPath( "personalia" ) );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        FormItems formItems = new FormItems();
        FormItemSet formItemSet = newFormItemSet().name( "personalia" ).label( "Personalia" ).build();
        formItems.add( formItemSet );
        formItemSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalFormItem personaliaEyeColourConfig = formItemSet.getInput( "eyeColour" );
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
    public void given_mixin_with_a_input_inside_a_set_when_getFormItem_with_path_to_input_then_exception_is_thrown()
    {
        // setup
        Input myInput = newInput().name( "myInput" ).type( InputTypes.TEXT_LINE ).build();
        FormItemSet mySet = newFormItemSet().name( "mySet" ).add( myInput ).build();
        Mixin myMixin = newMixin().module( ModuleName.from( "myModule" ) ).formItem( mySet ).build();

        FormItems formItems = new FormItems();
        formItems.add( newMixinReference().name( "mySet" ).typeInput().mixin( myMixin.getQualifiedName() ).build() );

        // exercise & verify
        try
        {
            formItems.getFormItem( new FormItemPath( "mySet.myInput" ) );
        }
        catch ( Exception e )
        {
            assertTrue( "Expected IllegalArgumentException", e instanceof IllegalArgumentException );
            assertEquals(
                "Cannot get formItem [mySet.myInput] because it's past a MixinReference [mySet], resolve the MixinReference first.",
                e.getMessage() );
        }
    }
}
