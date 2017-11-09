package com.enonic.xp.inputtype;

import org.junit.Test;

import static org.junit.Assert.*;

public class InputTypeNameTest
{
    @Test
    public void tostring()
    {
        assertEquals( "TextArea", InputTypeName.from( "TextArea" ).toString() );
    }

    @Test
    public void null_entry()
        throws Exception
    {
        assertFalse( InputTypeName.from( "fisk" ).equals( null ) );
    }

    @Test
    public void null_value()
        throws Exception
    {
        assertFalse( InputTypeName.from( "fisk" ).equals( InputTypeName.from( null ) ) );
    }


    @Test
    public void null_value_2()
        throws Exception
    {
        assertFalse( InputTypeName.from( null ).equals( InputTypeName.from( "fisk" ) ) );
    }

    @Test
    public void case_insensitive()
        throws Exception
    {
        assertTrue( InputTypeName.from( "HtmlArea" ).equals( InputTypeName.from( "htmlarea" ) ) );
    }
}
