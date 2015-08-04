package com.enonic.xp.form.inputtype;

import org.junit.Test;

import static org.junit.Assert.*;

public class InputTypeNameTest
{
    @Test
    public void tostring()
    {
        assertEquals( "TextArea", InputTypeName.from( "TextArea" ).toString() );
    }
}
