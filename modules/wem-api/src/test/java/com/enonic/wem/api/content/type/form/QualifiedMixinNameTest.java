package com.enonic.wem.api.content.type.form;

import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

public class QualifiedMixinNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new QualifiedMixinName( "myModule:myMixin" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new QualifiedMixinName( "myModule:myOtherMixin" ), new QualifiedMixinName( "myOtherModule:myMixin" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new QualifiedMixinName( "myModule:myMixin" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new QualifiedMixinName( "myModule:myMixin" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
