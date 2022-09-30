package com.enonic.xp.core.impl.content.serializer;

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

public class LayoutDataSerializerTest extends AbstractDataSerializerTest
{
    private LayoutComponentDataSerializer layoutDataSerializer;

    @BeforeEach
    public void setUp()
    {
        final RegionDataSerializer regionSerializer = new RegionDataSerializer( ComponentDataSerializerProvider.create().
            layoutDescriptorService( layoutDescriptorService ).
            partDescriptorService( partDescriptorService ).
            build() );

        this.layoutDataSerializer = new LayoutComponentDataSerializer( this.layoutDescriptorService, regionSerializer );
    }

    @Test
    public void testLayoutWithTwoRegions()
    {
        genAndCheckLayout( "left", "right");
    }

    @Test
    public void testLayoutWithRegionsWithSimilarNames()
    {
        genAndCheckLayout( "left", "left2");
    }

    private void genAndCheckLayout( final String regionName1, final String regionName2 )
    {
        final LayoutComponent layoutComponent = createLayoutComponent( regionName1, regionName2 );

        final PropertyTree layoutAsData = new PropertyTree();
        layoutDataSerializer.toData( layoutComponent, layoutAsData.getRoot() );
        final List<PropertySet> componentsAsData = layoutAsData.getProperties( COMPONENTS ).stream().filter( Property::hasNotNullValue ).
            map( Property::getSet ).collect( Collectors.toList() );
        final LayoutComponent parsedLayout = layoutDataSerializer.fromData( layoutAsData.getSet( COMPONENTS ), componentsAsData );

        assertEquals( layoutComponent, parsedLayout );
    }

    private LayoutComponent createLayoutComponent( final String regionName1, final String regionName2)
    {
        final String layoutName = "MyLayout";
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutDescriptor:name" );

        final Region region1 = Region.create().
            name( regionName1 ).
            build();

        final Region region2 = Region.create().
            name( regionName2 ).
            add( TextComponent.create().build() ).
            build();

        final LayoutRegions layoutRegions = LayoutRegions.create().add( region1 ).add( region2 ).build();

        Mockito.when( layoutDescriptorService.getByKey( layoutDescriptorKey ) ).thenReturn( LayoutDescriptor.create().
            key( layoutDescriptorKey ).
            displayName( layoutName ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().
            add( RegionDescriptor.create().
            name( regionName1 ).
            build() ).
            add( RegionDescriptor.create().
            name( regionName2 ).
            build() ).
            build() ).
            build() );

        return LayoutComponent.create().descriptor( layoutDescriptorKey ).regions( layoutRegions ).build();
    }

}
