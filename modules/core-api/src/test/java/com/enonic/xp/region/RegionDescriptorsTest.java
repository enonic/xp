package com.enonic.xp.region;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegionDescriptorsTest
{
    @Test
    public void testBuilder()
    {
        final RegionDescriptors regionDescriptors = RegionDescriptors.newRegionDescriptors().
            add( RegionDescriptor.newRegionDescriptor().name( "regionDescriptor1" ).build() ).
            add( RegionDescriptor.newRegionDescriptor().name( "regionDescriptor2" ).build() ).
            add( RegionDescriptor.newRegionDescriptor().name( "regionDescriptor3" ).build() ).
            build();

        assertEquals( 3, regionDescriptors.numberOfRegions() );
        assertNotNull( regionDescriptors.getRegionDescriptor( "regionDescriptor2" ) );
        assertNull( regionDescriptors.getRegionDescriptor( "regionDescriptor" ) );
    }

}
