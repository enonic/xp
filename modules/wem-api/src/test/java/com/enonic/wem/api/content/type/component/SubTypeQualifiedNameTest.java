package com.enonic.wem.api.content.type.component;

import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

public class SubTypeQualifiedNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new SubTypeQualifiedName( "myModule:mySubType" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new SubTypeQualifiedName( "myModule:myOtherSubType" ),
                    new SubTypeQualifiedName( "myOtherModule:mySubType" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new SubTypeQualifiedName( "myModule:mySubType" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new SubTypeQualifiedName( "myModule:mySubType" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
