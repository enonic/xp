package com.enonic.wem.api.content.type;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

public class ContentTypeQualifiedNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new ContentTypeQualifiedName( "myModule:myContentType" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new ContentTypeQualifiedName( "myModule:myOtherContentType" ),
                    new ContentTypeQualifiedName( "myOtherModule:myContentType" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new ContentTypeQualifiedName( "myModule:myContentType" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new ContentTypeQualifiedName( "myModule:myContentType" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

}
