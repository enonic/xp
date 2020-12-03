package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputTypeNameTest
{
    @Test
    public void toString_keeps_case()
    {
        assertEquals( "TextArea", InputTypeName.from( "TextArea" ).toString() );
    }

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( InputTypeName.class ).withIgnoredFields( "name" ).withNonnullFields( "lowercaseName" ).verify();
    }

    @Test
    public void case_insensitive()
    {
        assertEquals( InputTypeName.from( "htmlarea" ), InputTypeName.from( "HtmlArea" ) );
    }
}
