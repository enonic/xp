package com.enonic.xp.core.impl.content.serializer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.content.page.AbstractDataSerializerTest;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
import com.enonic.xp.region.TextComponent;

import static com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer.COMPONENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LayoutDataSerializerTest
    extends AbstractDataSerializerTest
{
    private LayoutComponentDataSerializer layoutDataSerializer;

    @BeforeEach
    public void setUp()
    {
        final RegionDataSerializer regionSerializer = new RegionDataSerializer( new ComponentDataSerializerProvider() );

        this.layoutDataSerializer = new LayoutComponentDataSerializer( regionSerializer );
    }

    @Test
    public void testLayoutWithTwoRegions()
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutDescriptor:name" );
        genAndCheckLayout( layoutDescriptorKey, "left", "right" );
    }

    @Test
    public void testLayoutWithRegionsWithSimilarNames()
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutDescriptor:name" );
        genAndCheckLayout( layoutDescriptorKey, "left", "left2" );
    }

    @Test
    public void testLayoutWithDescriptorServiceNotAvailable()
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutDescriptor:name" );
        genAndCheckLayout( layoutDescriptorKey, "up", "down" );
    }

    @Test
    public void testNoComponents()
    {
        final LayoutComponent parsedLayout = layoutDataSerializer.fromData( new PropertyTree().newSet() );
        assertTrue(parsedLayout.getRegions().isEmpty());
    }

    private void genAndCheckLayout( final DescriptorKey layoutDescriptorKey, final String... regionNames )
    {
        final LayoutComponent layoutComponent = createLayoutComponent( layoutDescriptorKey, regionNames );
        final PropertyTree layoutAsData = new PropertyTree();

        layoutDataSerializer.toData( layoutComponent, layoutAsData.getRoot() );

        final List<PropertySet> componentsAsData = layoutAsData.getProperties( COMPONENTS )
            .stream()
            .filter( Property::hasNotNullValue )
            .map( Property::getSet )
            .collect( Collectors.toList() );

        final LayoutComponent parsedLayout = layoutDataSerializer.fromData( layoutAsData.getSet( COMPONENTS ), componentsAsData );

        assertEquals( layoutComponent, parsedLayout );
    }

    private LayoutComponent createLayoutComponent( final DescriptorKey layoutDescriptorKey, final String... regionNames )
    {
        final Regions.Builder regionsBuilder = Regions.create();
        Arrays.stream( regionNames )
            .map( regionName -> Region.create().name( regionName ).add( TextComponent.create().build() ).build() )
            .forEach( regionsBuilder::add );

        return LayoutComponent.create().descriptor( layoutDescriptorKey ).regions( regionsBuilder.build() ).build();
    }
}
