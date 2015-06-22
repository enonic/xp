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
                return RegionDescriptor.newRegionDescriptor().name( "regionDescriptor" ).build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {

                return new Object[]{RegionDescriptor.newRegionDescriptor().name( "regionDescriptor2" ).build(),
                    RegionDescriptor.newRegionDescriptor().name( "RegionDescriptor" ).build(), new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RegionDescriptor.newRegionDescriptor().name( "regionDescriptor" ).build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RegionDescriptor.newRegionDescriptor().name( "regionDescriptor" ).build();
            }
        };

        equalsTest.assertEqualsAndHashCodeContract();
    }
}
