package com.enonic.wem.api.schema.mixin;

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
                return new QualifiedMixinName( "mymodule:my_mixin" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new QualifiedMixinName( "mymodule:my_other_mixin" ),
                    new QualifiedMixinName( "myothermodule:my_mixin" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new QualifiedMixinName( "mymodule:my_mixin" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new QualifiedMixinName( "mymodule:my_mixin" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
