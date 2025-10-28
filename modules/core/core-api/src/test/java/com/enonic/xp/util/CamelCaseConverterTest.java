package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CamelCaseConverterTest
{
    @Test
    void with_space()
    {
        assertEquals( "withSpace", CamelCaseConverter.defaultConvert( "with space" ) );
    }

    @Test
    void with_several_spaces()
    {
        assertEquals( "withSeveralSpaces", CamelCaseConverter.defaultConvert( "with several   spaces" ) );
    }

    @Test
    void with_colon()
    {
        assertEquals( "withColon", CamelCaseConverter.defaultConvert( "with:colon" ) );
    }

    @Test
    void with_space_and_colon()
    {
        assertEquals( "withSpaceAndColon", CamelCaseConverter.defaultConvert( "with space : and colon" ) );
    }

    @Test
    void capitalized_first_letter_space_and_number()
    {
        assertEquals( "component2", CamelCaseConverter.defaultConvert( "Component 2" ) );
    }

}
