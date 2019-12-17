package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CamelCaseConverterTest
{
    @Test
    public void with_space()
    {
        assertEquals( "withSpace", CamelCaseConverter.defaultConvert( "with space" ) );
    }

    @Test
    public void with_several_spaces()
    {
        assertEquals( "withSeveralSpaces", CamelCaseConverter.defaultConvert( "with several   spaces" ) );
    }

    @Test
    public void with_colon()
    {
        assertEquals( "withColon", CamelCaseConverter.defaultConvert( "with:colon" ) );
    }

    @Test
    public void with_space_and_colon()
    {
        assertEquals( "withSpaceAndColon", CamelCaseConverter.defaultConvert( "with space : and colon" ) );
    }

    @Test
    public void capitalized_first_letter_space_and_number()
    {
        assertEquals( "component2", CamelCaseConverter.defaultConvert( "Component 2" ) );
    }

}
