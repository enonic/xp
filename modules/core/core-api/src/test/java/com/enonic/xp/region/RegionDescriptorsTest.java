package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegionDescriptorsTest
{
    @Test
    void testBuilder()
    {
        final RegionDescriptors regionDescriptors = RegionDescriptors.create().
            add( RegionDescriptor.create().name( "regionDescriptor1" ).build() ).
            add( RegionDescriptor.create().name( "regionDescriptor2" ).build() ).
            add( RegionDescriptor.create().name( "regionDescriptor3" ).build() ).
            build();

        assertEquals( 3, regionDescriptors.numberOfRegions() );
    }

}
