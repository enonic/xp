package com.enonic.wem.api;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

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
}
