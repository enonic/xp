package com.enonic.xp.form;


import org.junit.Test;

import com.enonic.xp.form.inputtype.InputTypeName;

import static org.junit.Assert.*;

public class InputTest
{
    @Test
    public void copy()
    {
        // setup
        Input original = Input.create().name( "myInput" ).label( "Input" ).inputType( InputTypeName.TEXT_LINE ).build();

        // exercise
        Input copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "myInput", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertSame( original.getCustomText(), copy.getCustomText() );
        assertSame( original.getInputType(), copy.getInputType() );
    }

    @Test
    public void toInput_given_FormItem_of_type_Input_then_Input_is_returned()
    {
        // setup
        FormItem formItem = Input.create().label( "Input" ).name( "myInput" ).inputType( InputTypeName.TEXT_LINE ).build();

        // exercise
        Input input = formItem.toInput();

        // verify
        assertSame( formItem, input );
    }

    @Test
    public void toInput_given_FormItem_of_type_FormItemSet_then_exception_is_thrown()
    {
        // setup
        FormItem formItem = FormItemSet.create().name( "mySet" ).build();

        // exercise
        try
        {
            formItem.toInput();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "This FormItem [mySet] is not an Input: FormItemSet", e.getMessage() );
        }
    }
}
