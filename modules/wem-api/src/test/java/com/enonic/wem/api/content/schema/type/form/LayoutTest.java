package com.enonic.wem.api.content.schema.type.form;


import org.junit.Test;

import com.enonic.wem.api.content.schema.type.form.inputtype.InputTypes;

import static com.enonic.wem.api.content.schema.type.form.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.schema.type.form.Input.newInput;
import static org.junit.Assert.*;

public class LayoutTest
{

    @Test
    public void toLayout_given_FormItem_of_type_Layout_then_Layout_is_returned()
    {
        // setup
        FormItem formItem = newFieldSet().name( "myFieldSet" ).label( "My label" ).build();

        // exercise
        Layout layout = formItem.toLayout();

        // verify
        assertSame( formItem, layout );
    }

    @Test
    public void toLayout_given_FormItem_of_type_Input_then_exception_is_thrown()
    {
        // setup
        FormItem formItem = newInput().name( "myFieldSet" ).type( InputTypes.DATE ).label( "My label" ).build();

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
