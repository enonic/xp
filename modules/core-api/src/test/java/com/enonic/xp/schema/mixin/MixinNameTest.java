package com.enonic.xp.schema.mixin;

import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
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
                return MixinName.from( "mymodule:my_mixin" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{MixinName.from( "mymodule:my_other_mixin" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return MixinName.from( "mymodule:my_mixin" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return MixinName.from( ModuleKey.from( "mymodule" ), "my_mixin" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
