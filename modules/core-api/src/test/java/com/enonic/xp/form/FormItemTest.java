package com.enonic.xp.form;

import org.junit.Test;

import com.enonic.xp.inputtype.InputTypeName;

import static org.junit.Assert.*;

public class FormItemTest
{
    @Test
    public void setParent()
    {
        FormItemSet myParent = FormItemSet.create().name( "myParent" ).build();
        Input input = Input.create().name( "myField" ).label( "Field" ).inputType( InputTypeName.TEXT_LINE ).build();
        myParent.add( input );
        assertEquals( "myParent.myField", input.getPath().toString() );
    }
}
