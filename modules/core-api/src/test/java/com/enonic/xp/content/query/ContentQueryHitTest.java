package com.enonic.xp.content.query;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.support.AbstractEqualsTest;

public class ContentQueryHitTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new ContentQueryHit( 99, ContentId.from( "testId" ) );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new ContentQueryHit( 0, ContentId.from( "testId2" ) ),
                    new ContentQueryHit( 99, ContentId.from( "testId3" ) ), new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new ContentQueryHit( new Float( 99 ), ContentId.from( "testId" ) );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new ContentQueryHit( 99f, ContentId.from( "testId" ) );
            }
        };

        equalsTest.assertEqualsAndHashCodeContract();
    }
}
