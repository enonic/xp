package com.enonic.xp.form;


import org.junit.jupiter.api.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldSetTest
{
    @Test
    public void given_input_in_FieldSet_when_getPath_name_of_FieldSet_is_not_in_path()
    {
        FieldSet myFieldSet = FieldSet.create().
            label( "My FieldSet" ).
            addFormItem( Input.create().name( "myInput" ).label( "input" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        FieldSet myOuterFieldSet = FieldSet.create().
            label( "My Outer FieldSet" ).
            addFormItem( FieldSet.create().
            label( "My inner FieldSet" ).
            addFormItem( Input.create().name( "myInput2" ).label( "my input" ).inputType( InputTypeName.TEXT_LINE ).build() ).build() ).
            build();

        final Form form = Form.create().addFormItem( myFieldSet ).addFormItem( myOuterFieldSet ).build();

        assertEquals( "myInput2", form.getInput( "myInput2" ).getPath().toString() );
    }
}
