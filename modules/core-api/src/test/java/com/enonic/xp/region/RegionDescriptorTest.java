package com.enonic.xp.region;

import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

public class RegionDescriptorTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return RegionDescriptor.create().name( "regionDescriptor" ).build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {

                return new Object[]{RegionDescriptor.create().name( "regionDescriptor2" ).build(),
                    RegionDescriptor.create().name( "RegionDescriptor" ).build(), new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RegionDescriptor.create().name( "regionDescriptor" ).build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RegionDescriptor.create().name( "regionDescriptor" ).build();
            }
        };

        equalsTest.assertEqualsAndHashCodeContract();
    }
}
