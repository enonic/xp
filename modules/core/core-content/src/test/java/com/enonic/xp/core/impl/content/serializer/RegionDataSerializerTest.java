package com.enonic.xp.core.impl.content.serializer;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.content.page.AbstractDataSerializerTest;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;

import static org.junit.Assert.*;

public class RegionDataSerializerTest
    extends AbstractDataSerializerTest
{
    private RegionDataSerializer regionSerializer;

    @Before
    public void setUp()
    {
        super.setUp();

        this.regionSerializer = new RegionDataSerializer( ComponentDataSerializerProvider.create().
            contentService( contentService ).
            layoutDescriptorService( layoutDescriptorService ).
            partDescriptorService( partDescriptorService ).
            build() );
    }

    @Test
    public void region()
    {
        final PropertyTree myPartConfig = new PropertyTree();
        myPartConfig.addString( "some", "config" );
        final String layoutName = "MyOtherPart";
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "app-descr:layout-name" );

        final Region region = Region.create().
            name( "main" ).
            add( createPartComponent( "MyPart", "app-descr:part-name", myPartConfig ) ).
            add( ImageComponent.create().name( "Image" ).build() ).
            add( LayoutComponent.create().name( layoutName ).descriptor( layoutDescriptorKey ).build() ).
            build();

        Mockito.when( layoutDescriptorService.getByKey( layoutDescriptorKey ) ).thenReturn( LayoutDescriptor.create().
            key( layoutDescriptorKey ).
            displayName( layoutName ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().build() ).
            build() );

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