package com.enonic.wem.api.module;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ModuleNameTest
{
    @Test
    public void lower_case_is_legal()
    {
        assertEquals( "lowercaseislegal", ModuleName.from( "lowercaseislegal" ).toString() );
    }

    @Test
    public void special_char_dot_is_legal()
    {
        assertEquals( "special.char", ModuleName.from( "special.char" ).toString() );
    }

    @Test
    public void special_char_underscore_is_legal()
    {
        assertEquals( "special_char", ModuleName.from( "special_char" ).toString() );
    }

    @Test
    public void special_char_dash_is_legal()
    {
        assertEquals( "special-char", ModuleName.from( "special-char" ).toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void starting_with_special_char_dot_is_illegal()
    {
        assertEquals( ".special", ModuleName.from( ".special" ).toString() );
    }

    @Test
    public void starting_with_special_char_underscore_is_legal()
    {
        assertEquals( "_special", ModuleName.from( "_special" ).toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void starting_with_special_char_dash_is_illegal()
    {
        assertEquals( "-special", ModuleName.from( "-special" ).toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void upper_case_is_illegal()
    {
        ModuleName.from( "UpperCaseIsIllegal" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void norwegian_special_char_is_illegal()
    {
        ModuleName.from( "norwegianø" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void starting_with_digit_is_illegal()
    {
        assertEquals( "lowercaseislegal", ModuleName.from( "1start" ).toString() );
    }
}
