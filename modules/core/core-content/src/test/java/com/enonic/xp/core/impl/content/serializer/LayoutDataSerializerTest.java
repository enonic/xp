package com.enonic.xp.core.impl.content.serializer;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.content.page.AbstractDataSerializerTest;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
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
        final RegionDataSerializer regionSerializer = new RegionDataSerializer( ComponentDataSerializerProvider.create()
                                                                                    .layoutDescriptorService( layoutDescriptorService )
                                                                                    .partDescriptorService( partDescriptorService )
                                                                                    .build() );

        this.layoutDataSerializer = new LayoutComponentDataSerializer( this.layoutDescriptorService, regionSerializer );
    }

    @Test
    public void testLayoutWithTwoRegions()
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutDescriptor:name" );
        setupDescriptorService( layoutDescriptorKey, "left", "right" );
        genAndCheckLayout( layoutDescriptorKey, "left", "right" );
    }

    @Test
    public void testLayoutWithRegionsWithSimilarNames()
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutDescriptor:name" );
        setupDescriptorService( layoutDescriptorKey, "left", "left2" );
        genAndCheckLayout( layoutDescriptorKey, "left", "left2" );
    }

    @Test
    public void testLayoutWithDescriptorServiceNotAvailable()
    {
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutDescriptor:name" );
        setupDefaultDescriptor( layoutDescriptorKey );
        genAndCheckLayout( layoutDescriptorKey, "up", "down" );
    }

    @Test
    public void testNoComponents()
    {
        final LayoutComponent parsedLayout = layoutDataSerializer.fromData( new PropertySet() );
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
        final LayoutRegions.Builder layoutRegionsBuilder = LayoutRegions.create();
        Arrays.stream( regionNames ).map( regionName -> Region.create().name( regionName ).add( TextComponent.create().build() ).build() ).forEach( layoutRegionsBuilder::add );

        return LayoutComponent.create().descriptor( layoutDescriptorKey ).regions( layoutRegionsBuilder.build() ).build();
    }

    private void setupDescriptorService( final DescriptorKey key, final String... regionNames )
    {
        final String layoutName = "MyLayout";
        final RegionDescriptors.Builder builder = RegionDescriptors.create();
        Arrays.stream( regionNames ).map( regionName -> RegionDescriptor.create().name( regionName ).build() ).forEach( builder::add );

        Mockito.when( layoutDescriptorService.getByKey( key ) )
            .thenReturn( LayoutDescriptor.create()
                             .key( key )
                             .modifiedTime( Instant.now() )
                             .displayName( layoutName )
                             .config( Form.create().build() )
                             .regions( builder.build() )
                             .build() );
    }

    private void setupDefaultDescriptor( final DescriptorKey key )
    {
        Mockito.when( layoutDescriptorService.getByKey( key ) )
            .thenReturn( LayoutDescriptor.create()
                             .key( key )
                             .config( Form.create().build() )
                             .regions( RegionDescriptors.create().build() )
                             .build() );
    }
}
