package com.enonic.xp.form;


import org.junit.jupiter.api.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FormItemSetTest
{
    @Test
    public void getInput()
    {
        // setup
        FormItemSet formItemSet = FormItemSet.create()
            .name( "mySet" )
            .label( "Label" )
            .multiple( true )
            .addFormItem( Input.create().name( "myInput" ).label( "input" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();

        // exercise

        // verify
        assertEquals( "mySet.myInput", formItemSet.getInput( "myInput" ).getPath().toString() );
    }

    @Test
    public void getFormFragment()
    {
        // setup
        FormItemSet formItemSet = FormItemSet.create()
            .name( "mySet" )
            .label( "Label" )
            .multiple( true )
            .addFormItem( FormFragment.create().formFragment( "myapplication:myFormFragment" ).build() )
            .build();

        // exercise

        // verify
        assertEquals( "mySet.myFormFragment", formItemSet.getFormFragment( "myFormFragment" ).getPath().toString() );
    }


    @Test
    public void getFormItemSet()
    {
        // setup
        FormItemSet myInnermostSet = FormItemSet.create().name( "myInnermostSet" ).label( "Label" ).multiple( true ).build();
        FormItemSet myInnerSet =
            FormItemSet.create().name( "myInnerSet" ).label( "Label" ).multiple( true ).addFormItem( myInnermostSet ).build();
        FormItemSet myOuterSet =
            FormItemSet.create().name( "myOuterSet" ).label( "Label" ).multiple( true ).addFormItem( myInnerSet ).build();

        // exercise
        assertEquals( "myOuterSet.myInnerSet", myOuterSet.getFormItemSet( "myInnerSet" ).getPath().toString() );
        assertEquals( "myOuterSet.myInnerSet.myInnermostSet",
                      myOuterSet.getFormItemSet( "myInnerSet.myInnermostSet" ).getPath().toString() );
        assertEquals( "myOuterSet.myInnerSet.myInnermostSet",
                      myOuterSet.getFormItemSet( "myInnerSet" ).getFormItemSet( "myInnermostSet" ).getPath().toString() );
    }

    @Test
    public void given_FormItemSet_with_child_Input_when_getInput_with_name_of_child_then_child_is_returned()
    {
        // exercise
        FormItemSet parent = FormItemSet.create().name( "parent" ).label( "Parent" )
            .addFormItem( Input.create().name( "child" ).label( "child" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();

        // verify
        assertEquals( "parent.child", parent.getInput( "child" ).getPath().toString() );
    }

    @Test
    public void toFormItemSet_given_FormItem_of_type_FormItemSet_then_FormItemSet_is_returned()
    {
        // setup
        FormItem formItem = FormItemSet.create().name( "myFieldSet" ).label( "My label" ).build();

        // exercise
        FormItemSet formItemSet = formItem.toFormItemSet();

        // verify
        assertSame( formItem, formItemSet );
    }

    @Test
    public void toFormItemSet_given_FormItem_of_type_Input_then_exception_is_thrown()
    {
        // setup
        FormItem formItem =
            Input.create().name( "myFieldSet" ).label( "field set" ).inputType( InputTypeName.DATE ).label( "My label" ).build();

        // exercise
        try
        {
            formItem.toFormItemSet();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "This FormItem [myFieldSet] is not a FormItemSet: Input", e.getMessage() );
        }
    }

    @Test
    public void copy()
    {
        // setup
        FormItemSet formItemSet = FormItemSet.create().name( "myFormItemSet" ).label( "Label" ).multiple( true )
            .addFormItem( Input.create().name( "myField" ).label( "my field" ).inputType( InputTypeName.TEXT_LINE ).build() )
            .build();

        // exercise
        FormItemSet copy = formItemSet.copy();

        // verify
        assertNotSame( formItemSet, copy );
        assertNull( copy.getParent() );
        assertEquals( "myFormItemSet", copy.getName() );
        assertSame( formItemSet.getName(), copy.getName() );
        assertSame( formItemSet.getLabel(), copy.getLabel() );
        assertNotSame( formItemSet.getInput( "myField" ), copy.getInput( "myField" ) );
        assertEquals( "myFormItemSet.myField", copy.getInput( "myField" ).getPath().toString() );
        assertEquals( formItemSet.getPath(), copy.getInput( "myField" ).getParent().getPath() );
        assertEquals( formItemSet, copy );
    }
}
