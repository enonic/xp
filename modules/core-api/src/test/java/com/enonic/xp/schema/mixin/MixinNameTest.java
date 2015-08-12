package com.enonic.xp.schema.mixin;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractEqualsTest;

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
                return MixinName.from( "myapplication:my_mixin" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{MixinName.from( "myapplication:my_other_mixin" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return MixinName.from( "myapplication:my_mixin" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return MixinName.from( ApplicationKey.from( "myapplication" ), "my_mixin" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
