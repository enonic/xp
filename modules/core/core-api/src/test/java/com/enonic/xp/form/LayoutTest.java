package com.enonic.xp.form;


import org.junit.jupiter.api.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class LayoutTest
{

    @Test
    public void toLayout_given_FormItem_of_type_Layout_then_Layout_is_returned()
    {
        // setup
        FormItem formItem = FieldSet.create().name( "myFieldSet" ).label( "My label" ).build();

        // exercise
        Layout layout = formItem.toLayout();

        // verify
        assertSame( formItem, layout );
    }

    @Test
    public void toLayout_given_FormItem_of_type_Input_then_exception_is_thrown()
    {
        // setup
        FormItem formItem = Input.create().name( "myFieldSet" ).inputType( InputTypeName.DATE ).label( "My label" ).build();

        // exercise
        try
        {
            formItem.toLayout();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "This FormItem [myFieldSet] is not a Layout: Input", e.getMessage() );
        }
    }
}
