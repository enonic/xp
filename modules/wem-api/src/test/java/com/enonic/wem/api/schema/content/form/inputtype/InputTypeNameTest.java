package com.enonic.wem.api.schema.content.form.inputtype;


import org.junit.Test;

import com.acme.DummyCustomInputType;

import static org.junit.Assert.*;

public class InputTypeNameTest
{
    @Test
    public void isCustom()
    {
        assertFalse( InputTypeName.from( "TextArea" ).isCustom() );
        assertTrue( InputTypeName.from( "custom:MyTextArea" ).isCustom() );
    }

    @Test
    public void tostring()
    {
        assertEquals( "TextArea", InputTypeName.from( "TextArea" ).toString() );
        assertTrue( "custom:MyTextArea", InputTypeName.from( "custom:MyTextArea" ).isCustom() );
    }

    @Test
    public void from_InputType()
    {
        assertTrue( InputTypeName.from( new DummyCustomInputType() ).isCustom() );
        assertFalse( InputTypeName.from( InputTypes.TEXT_LINE ).isCustom() );

        assertEquals( InputTypes.TEXT_LINE.getName(), InputTypeName.from( InputTypes.TEXT_LINE ).toString() );
        assertEquals( "custom:DummyCustomInputType", InputTypeName.from( new DummyCustomInputType() ).toString() );
    }
}
