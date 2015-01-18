package com.enonic.wem.api.form;

import org.junit.Test;

import com.enonic.wem.api.form.inputtype.InputTypes;

import static com.enonic.wem.api.form.Input.newInput;
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
