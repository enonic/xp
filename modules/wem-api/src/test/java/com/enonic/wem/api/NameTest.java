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
    public void test_ensure_valid_name()
    {
        final String testName = " test Name ";
        final String testResult = "test-name";
        assertEquals( testResult, Name.ensureValidName( testName ) );
    }

    @Test
    public void test_ensure_valid_spesial_chars1()
    {
        final String testName = "Åse Kålrot";
        final String testResult = "se-klrot";
        assertEquals( testResult, Name.ensureValidName( testName ) );
    }

    @Test
    public void test_ensure_valid_spesial_chars2()
    {
        final String testName = "t@e&st^-$cha$r@s $1 ";
        final String testResult = "test-chars-1";
        assertEquals( testResult, Name.ensureValidName( testName ) );
    }

    @Test
    public void test_ensure_valid_name_empty()
    {
        final String testName = "";
        final String testResult = "";
        assertEquals( testResult, Name.ensureValidName( testName ) );
    }

    @Test
    public void test_ensure_valid_name_null()
    {
        final String testName = null;
        final String testResult = "";
        assertEquals( testResult, Name.ensureValidName( testName ) );
    }
}
