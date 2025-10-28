package com.enonic.xp.issue;

import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

class IssueNameTest
{

    @Test
    void testEquals()
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
    void testIllegalName1()
    {
        assertThrows(NullPointerException.class, () -> IssueName.from( null ));
    }

    @Test
    void testIllegalName2()
    {
        assertThrows(IllegalArgumentException.class, () -> IssueName.from( "" ) );
    }

    @Test
    void testIllegalName3()
    {
        assertThrows(IllegalArgumentException.class, () -> IssueName.from( "a/" ) );
    }
}
