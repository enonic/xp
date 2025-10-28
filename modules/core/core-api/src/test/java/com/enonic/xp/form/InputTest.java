package com.enonic.xp.form;


import org.junit.jupiter.api.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class InputTest
{
    @Test
    void copy()
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
        assertSame( original.getInputType(), copy.getInputType() );
    }

    @Test
    void toInput_given_FormItem_of_type_Input_then_Input_is_returned()
    {
        // setup
        FormItem formItem = Input.create().label( "Input" ).name( "myInput" ).inputType( InputTypeName.TEXT_LINE ).build();

        // exercise
        Input input = formItem.toInput();

        // verify
        assertSame( formItem, input );
    }

    @Test
    void toInput_given_FormItem_of_type_FormItemSet_then_exception_is_thrown()
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
