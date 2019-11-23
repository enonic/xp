package com.enonic.xp.form;

import org.junit.jupiter.api.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static com.enonic.xp.form.FormItemPath.ELEMENT_DIVIDER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormItemTest
{
    @Test
    public void setParent()
    {
        FormItemSet myParent = FormItemSet.create().name( "myParent" ).build();
        Input input = Input.create().name( "myField" ).label( "Field" ).inputType( InputTypeName.TEXT_LINE ).build();
        myParent.add( input );
        assertEquals( "myParent" + ELEMENT_DIVIDER + "myField", input.getPath().toString() );
    }
}
