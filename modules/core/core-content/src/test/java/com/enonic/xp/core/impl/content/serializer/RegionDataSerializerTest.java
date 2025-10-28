package com.enonic.xp.core.impl.content.serializer;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.page.AbstractDataSerializerTest;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegionDataSerializerTest
    extends AbstractDataSerializerTest
{
    private RegionDataSerializer regionSerializer;

    @BeforeEach
    void setUp()
    {
        this.regionSerializer = new RegionDataSerializer( new ComponentDataSerializerProvider() );
    }

    @Test
    void region()
    {
        final PropertyTree myPartConfig = new PropertyTree();
        myPartConfig.addString( "some", "config" );
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "app-descr:layout-name" );

        final Region region = Region.create().
            name( "main" ).
            add( createPartComponent( "MyPart", "app-descr:part-name", myPartConfig ) ).
            add( ImageComponent.create().build() ).
            add( LayoutComponent.create().descriptor( layoutDescriptorKey ).build() ).
            build();

        final PropertyTree regionAsData = new PropertyTree();

        // exercise
        regionSerializer.toData( region, regionAsData.getRoot() );
        final RegionDescriptor regionDescriptor = RegionDescriptor.create().name( "main" ).build();
        final List<PropertySet> components =
            regionAsData.getProperties( ComponentDataSerializer.COMPONENTS ).stream().map( item -> item.getSet() ).collect(
                Collectors.toList() );

        final Region parsedRegion = regionSerializer.fromData( regionDescriptor, ComponentPath.DIVIDER, components );

        // verify
        assertEquals( region, parsedRegion );
    }
}
