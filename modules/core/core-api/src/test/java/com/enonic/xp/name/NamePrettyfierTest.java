package com.enonic.xp.name;

import org.junit.Test;

import static org.junit.Assert.*;

public class NamePrettyfierTest
{

    @Test
    public void test_to_lower_and_space_to_hyphens()
    {
        assertEquals( "seed-savers", NamePrettyfier.create( "Seed Savers" ) );
    }

    @Test
    public void test_ensure_valid_name_spaces_are_trimmed()
    {
        assertEquals( "test-name", NamePrettyfier.create( " test Name " ) );
    }

    @Test
    public void test_ensure_valid_special_chars1()
    {
        assertEquals( "ase-kalrot", NamePrettyfier.create( "Åse Kålrot" ) );
    }

    @Test
    public void test_ensure_valid_special_chars3()
    {
        assertEquals( "test-stuff-here", NamePrettyfier.create( "test/stuff/here " ) );
    }

    @Test
    public void test_ensure_valid_special_chars2()
    {
        assertEquals( "t-e-st-cha-r-s-1", NamePrettyfier.create( "t@e&st^-$cha$r@s $1 " ) );
    }

    @Test
    public void removeInvisibleChars()
    {
        assertEquals( "teststuffhere", NamePrettyfier.create( "Test\u0081Stuff\u0082Here\u0083" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ensure_valid_name_empty()
    {
        assertEquals( "", NamePrettyfier.create( "" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ensure_valid_name_space()
    {
        assertEquals( "", NamePrettyfier.create( " " ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ensure_valid_name_null()
    {
        assertEquals( "", NamePrettyfier.create( null ) );
    }

}