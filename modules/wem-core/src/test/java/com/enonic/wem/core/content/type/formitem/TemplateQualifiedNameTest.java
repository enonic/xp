package com.enonic.wem.core.content.type.formitem;

import org.junit.Test;

import com.enonic.wem.core.content.AbstractEqualsTest;

public class TemplateQualifiedNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new TemplateQualifiedName( "myModule:myTemplate" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new TemplateQualifiedName( "myModule:myOtherTemplate" ),
                    new TemplateQualifiedName( "myOtherModule:myTemplate" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new TemplateQualifiedName( "myModule:myTemplate" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new TemplateQualifiedName( "myModule:myTemplate" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
