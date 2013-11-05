package com.enonic.wem.api.entity;


import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;

import static junit.framework.Assert.assertEquals;

public class EntityIdsTest
{
    @Test
    public void from_multiple_strings()
    {

        assertEquals( "[aaa]", EntityIds.from( "aaa" ).toString() );
        assertEquals( "[aaa, bbb]", EntityIds.from( "aaa", "bbb" ).toString() );
        assertEquals( "[aaa, bbb, ccc]", EntityIds.from( "aaa", "bbb", "ccc" ).toString() );
    }

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return EntityIds.from( "aaa", "bbb" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{EntityIds.from( "aaa" ), EntityIds.from( "aaa", "ccc" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return EntityIds.from( "aaa", "bbb" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return EntityIds.from( "aaa", "bbb" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
