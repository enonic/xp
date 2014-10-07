package com.enonic.wem.api;


import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;

import static org.junit.Assert.*;

public class NameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new Name( "name" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new Name( "other" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new Name( "name" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new Name( "name" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void test_ensure_valid_name_spaces_are_trimmed()
    {
        assertEquals( "test-name", Name.ensureValidName( " test Name " ) );
    }

    @Test
    public void test_ensure_valid_spesial_chars1()
    {
        assertEquals( "se-klrot", Name.ensureValidName( "Åse Kålrot" ) );
    }

    @Test
    public void test_ensure_valid_spesial_chars2()
    {
        assertEquals( "test-chars-1", Name.ensureValidName( "t@e&st^-$cha$r@s $1 " ) );
    }

    @Test
    public void test_ensure_valid_name_empty()
    {
        assertEquals( "", Name.ensureValidName( "" ) );
    }

    @Test
    public void test_ensure_valid_name_space()
    {
        assertEquals( "", Name.ensureValidName( " " ) );
    }

    @Test
    public void test_ensure_valid_name_null()
    {
        assertEquals( "", Name.ensureValidName( null ) );
    }
}
