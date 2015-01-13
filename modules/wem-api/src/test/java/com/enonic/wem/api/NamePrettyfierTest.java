package com.enonic.wem.api;

import org.junit.Test;

import static org.junit.Assert.*;

public class NamePrettyfierTest
{
    @Test
    public void test_ensure_valid_name_spaces_are_trimmed()
    {
        assertEquals( "test-name", NamePrettyfier.prettifyName( " test Name ", true ) );
    }

    @Test
    public void test_ensure_valid_special_chars1_transliterate()
    {
        assertEquals( "ase-kalrot", NamePrettyfier.prettifyName( "Åse Kålrot", true ) );
    }

    @Test
    public void test_ensure_valid_special_chars1()
    {
        assertEquals( "åse-kålrot", NamePrettyfier.prettifyName( "Åse Kålrot", false ) );
    }

    @Test
    public void test_ensure_valid_special_chars3()
    {
        assertEquals( "test-stuff-here", NamePrettyfier.prettifyName( "test/stuff/here ", true ) );
    }

    @Test
    public void test_ensure_valid_special_chars2()
    {
        assertEquals( "t-e-st-cha-r-s-1", NamePrettyfier.prettifyName( "t@e&st^-$cha$r@s $1 ", true ) );
    }

    @Test
    public void removeInvisibleChars()
    {
        assertEquals( "teststuffhere", NamePrettyfier.prettifyName( "Test\u0081Stuff\u0082Here\u0083", true ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ensure_valid_name_empty()
    {
        assertEquals( "", NamePrettyfier.prettifyName( "", true ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ensure_valid_name_space()
    {
        assertEquals( "", NamePrettyfier.prettifyName( " ", true ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ensure_valid_name_null()
    {
        assertEquals( "", NamePrettyfier.prettifyName( null, true ) );
    }

}