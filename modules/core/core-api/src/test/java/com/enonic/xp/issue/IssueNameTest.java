package com.enonic.xp.issue;

import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void testIllegalName1()
    {
        assertThrows(NullPointerException.class, () -> IssueName.from( null ));
    }

    @Test
    public void testIllegalName2()
    {
        assertThrows(IllegalArgumentException.class, () -> IssueName.from( "" ) );
    }

    @Test
    public void testIllegalName3()
    {
        assertThrows(IllegalArgumentException.class, () -> IssueName.from( "a/" ) );
    }
}
