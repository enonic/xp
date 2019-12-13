package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegionDescriptorsTest
{
    @Test
    public void testBuilder()
    {
        final RegionDescriptors regionDescriptors = RegionDescriptors.create().
            add( RegionDescriptor.create().name( "regionDescriptor1" ).build() ).
            add( RegionDescriptor.create().name( "regionDescriptor2" ).build() ).
            add( RegionDescriptor.create().name( "regionDescriptor3" ).build() ).
            build();

        assertEquals( 3, regionDescriptors.numberOfRegions() );
        assertNotNull( regionDescriptors.getRegionDescriptor( "regionDescriptor2" ) );
        assertNull( regionDescriptors.getRegionDescriptor( "regionDescriptor" ) );
    }

}
