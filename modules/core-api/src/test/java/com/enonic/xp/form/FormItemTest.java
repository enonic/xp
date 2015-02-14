package com.enonic.xp.form;

import org.junit.Test;

import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;

import static com.enonic.xp.form.Input.newInput;
import static org.junit.Assert.*;

public class FormItemTest
{
    @Test
    public void setParent()
    {
        FormItemSet myParent = FormItemSet.newFormItemSet().name( "myParent" ).build();
        Input input = newInput().name( "myField" ).inputType( InputTypes.TEXT_LINE ).build();
        myParent.add( input );
        assertEquals( "myParent.myField", input.getPath().toString() );
    }
}
