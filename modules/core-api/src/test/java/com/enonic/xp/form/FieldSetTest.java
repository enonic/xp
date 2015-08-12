package com.enonic.xp.form;


import org.junit.Test;

import com.enonic.xp.form.inputtype.InputTypeName;

import static org.junit.Assert.*;

public class FieldSetTest
{
    @Test
    public void given_input_in_FieldSet_when_getPath_name_of_FieldSet_is_not_in_path()
    {
        FieldSet myFieldSet = FieldSet.create().
            label( "My FieldSet" ).
            name( "myFieldSet" ).
            addFormItem( Input.create().name( "myInput" ).label( "input" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();

        assertEquals( "myInput", myFieldSet.getInput( "myInput" ).getPath().toString() );

        FieldSet myOuterFieldSet = FieldSet.create().
            label( "My Outer FieldSet" ).
            name( "myOuterFieldSet" ).
            addFormItem( FieldSet.create().
                label( "My inner FieldSet" ).
                name( "myInnerFieldSet" ).
                addFormItem( Input.create().name( "myInput" ).label( "my input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        assertEquals( "myInput", myOuterFieldSet.getInput( "myInput" ).getPath().toString() );
    }
}
