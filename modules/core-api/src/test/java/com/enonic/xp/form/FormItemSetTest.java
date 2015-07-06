package com.enonic.xp.form;


import org.junit.Test;

import com.enonic.xp.form.inputtype.InputTypes;

import static org.junit.Assert.*;

public class FormItemSetTest
{
    @Test
    public void getInput()
    {
        // setup
        FormItemSet formItemSet = FormItemSet.create().name( "mySet" ).label( "Label" ).multiple( true ).build();
        formItemSet.add( Input.create().name( "myInput" ).label( "input" ).inputType( InputTypes.TEXT_LINE ).build() );

        // exercise

        // verify
        assertEquals( "mySet.myInput", formItemSet.getInput( "myInput" ).getPath().toString() );
        assertEquals( "mySet.myInput", formItemSet.getInput( FormItemPath.from( "myInput" ) ).getPath().toString() );
    }

    @Test
    public void getInlineMixin()
    {
        // setup
        FormItemSet formItemSet = FormItemSet.create().name( "mySet" ).label( "Label" ).multiple( true ).build();
        formItemSet.add( InlineMixin.create().mixin( "mymodule:mymixin" ).build() );

        // exercise

        // verify
        assertEquals( "mySet.mymixin", formItemSet.getInlineMixin( "mymixin" ).getPath().toString() );
        assertEquals( "mySet.mymixin", formItemSet.getInlineMixin( FormItemPath.from( "mymixin" ) ).getPath().toString() );
    }


    @Test
    public void getFormItemSet()
    {
        // setup
        FormItemSet myOuterSet = FormItemSet.create().name( "myOuterSet" ).label( "Label" ).multiple( true ).build();
        FormItemSet myInnerSet = FormItemSet.create().name( "myInnerSet" ).label( "Label" ).multiple( true ).build();
        FormItemSet myInnermostSet = FormItemSet.create().name( "myInnermostSet" ).label( "Label" ).multiple( true ).build();
        myInnerSet.add( myInnermostSet );
        myOuterSet.add( myInnerSet );

        // exercise
        assertEquals( "myOuterSet.myInnerSet", myOuterSet.getFormItemSet( "myInnerSet" ).getPath().toString() );
        assertEquals( "myOuterSet.myInnerSet", myOuterSet.getFormItemSet( FormItemPath.from( "myInnerSet" ) ).getPath().toString() );
        assertEquals( "myOuterSet.myInnerSet.myInnermostSet",
                      myOuterSet.getFormItemSet( "myInnerSet.myInnermostSet" ).getPath().toString() );
        assertEquals( "myOuterSet.myInnerSet.myInnermostSet",
                      myOuterSet.getFormItemSet( "myInnerSet" ).getFormItemSet( "myInnermostSet" ).getPath().toString() );
    }

    @Test
    public void given_FormItemSet_with_child_Input_when_getInput_with_name_of_child_then_child_is_returned()
    {
        // exercise
        FormItemSet parent = FormItemSet.create().name( "parent" ).label( "Parent" ).build();
        parent.add( Input.create().name( "child" ).label( "child" ).inputType( InputTypes.TEXT_LINE ).build() );

        // verify
        assertEquals( "parent.child", parent.getInput( "child" ).getPath().toString() );
    }

    @Test
    public void given_FormItemSet_with_child_Input_when_getInput_with_name_of_child_then_child_is_returned2()
    {

        FormItemSet parent = FormItemSet.create().name( "parent" ).label( "Parent" ).build();
        parent.add( Input.create().name( "child" ).label( "child" ).inputType( InputTypes.TEXT_LINE ).build() );

        // exercise
        FormItemSet newParent = FormItemSet.create().name( "newParent" ).label( "New Parent" ).build();
        try
        {
            newParent.add( parent.getInput( "child" ) );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "formItem [child] already added to: parent", e.getMessage() );
        }
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
            Input.create().name( "myFieldSet" ).label( "field set" ).inputType( InputTypes.DATE ).label( "My label" ).build();

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
        FormItemSet formItemSet = FormItemSet.create().name( "myFormItemSet" ).label( "Label" ).multiple( true ).build();
        formItemSet.add( Input.create().name( "myField" ).label( "my field" ).inputType( InputTypes.TEXT_LINE ).build() );

        // exercise
        FormItemSet copy = formItemSet.copy();

        // verify
        assertNotSame( formItemSet, copy );
        assertEquals( "myFormItemSet", copy.getName() );
        assertSame( formItemSet.getName(), copy.getName() );
        assertSame( formItemSet.getLabel(), copy.getLabel() );
        assertNotSame( formItemSet.getFormItems(), copy.getFormItems() );
        assertNotSame( formItemSet.getInput( "myField" ), copy.getInput( "myField" ) );
        assertEquals( "myFormItemSet.myField", copy.getInput( "myField" ).getPath().toString() );
        assertEquals( formItemSet.getPath(), copy.getInput( "myField" ).getParent().getPath() );
    }
}
