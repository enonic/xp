package com.enonic.wem.api.schema.mixin;

import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;

public class MixinNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return MixinName.from( "my_mixin" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{MixinName.from( "my_other_mixin" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return MixinName.from( "my_mixin" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return MixinName.from( "my_mixin" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
