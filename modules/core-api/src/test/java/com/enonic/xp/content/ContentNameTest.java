package com.enonic.xp.content;


import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

public class ContentNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return ContentName.from( "mycontent" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{ContentName.from( "myothercontent" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return ContentName.from( "mycontent" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return ContentName.from( "mycontent" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
