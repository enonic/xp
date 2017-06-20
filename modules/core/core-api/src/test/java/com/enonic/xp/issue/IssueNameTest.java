package com.enonic.xp.issue;

import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

public class IssueNameTest
{

    @Test
    public void testEquals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return IssueName.from( "myissue" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{IssueName.from( "myotherissue" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return IssueName.from( "myissue" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return IssueName.from( "myissue" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test(expected = NullPointerException.class)
    public void testIllegalName1()
    {
        IssueName.from( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalName2()
    {
        IssueName.from( "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalName3()
    {
        IssueName.from( "a/" );
    }
}
