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
                return QualifiedMixinName.from( "mymodule:my_mixin" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{QualifiedMixinName.from( "mymodule:my_other_mixin" ),
                    QualifiedMixinName.from( "myothermodule:my_mixin" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return QualifiedMixinName.from( "mymodule:my_mixin" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return QualifiedMixinName.from( "mymodule:my_mixin" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
