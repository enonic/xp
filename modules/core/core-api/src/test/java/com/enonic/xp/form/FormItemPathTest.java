package com.enonic.xp.form;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormItemPathTest
{
    @Test
    void tostring()
    {
        assertEquals( "car", FormItemPath.from( "car" ).toString() );
        assertEquals( "car.model", FormItemPath.from( "car.model" ).toString() );
    }

    @Test
    void new_given_existing_formItemPath_and_name()
    {
        assertEquals( "car.model", FormItemPath.from( FormItemPath.from( "car" ), "model" ).toString() );
    }

    @Test
    void asNewWithoutFirstPathElement()
    {
        assertEquals( "", FormItemPath.from( "first" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second", FormItemPath.from( "first.second" ).asNewWithoutFirstPathElement().toString() );
        assertEquals( "second.third", FormItemPath.from( "first.second.third" ).asNewWithoutFirstPathElement().toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( FormItemPath.class ).withNonnullFields( "elements" ).verify();
    }
}
