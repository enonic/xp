package com.enonic.wem.api.content.type.form;

import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

public class QualifiedSubTypeNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new QualifiedSubTypeName( "myModule:mySubType" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new QualifiedSubTypeName( "myModule:myOtherSubType" ),
                    new QualifiedSubTypeName( "myOtherModule:mySubType" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new QualifiedSubTypeName( "myModule:mySubType" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new QualifiedSubTypeName( "myModule:mySubType" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
