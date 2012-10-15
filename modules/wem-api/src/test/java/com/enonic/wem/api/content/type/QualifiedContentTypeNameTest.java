package com.enonic.wem.api.content.type;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

public class QualifiedContentTypeNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new QualifiedContentTypeName( "myModule:myOtherContentType" ),
                    new QualifiedContentTypeName( "myOtherModule:myContentType" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

}
