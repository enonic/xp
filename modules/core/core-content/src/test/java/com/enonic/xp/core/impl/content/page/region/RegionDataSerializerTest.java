package com.enonic.xp.core.impl.content.page.region;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.enonic.xp.core.impl.content.serializer.ComponentDataSerializer;
import com.enonic.xp.core.impl.content.serializer.RegionDataSerializer;
import com.enonic.xp.core.impl.content.serializer.SerializedData;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;

import static org.junit.Assert.*;

public class RegionDataSerializerTest
{
    private RegionDataSerializer regionSerializer = new RegionDataSerializer();

    @Test
    public void region()
    {
        PropertyTree myPartConfig = new PropertyTree();
        myPartConfig.addString( "some", "config" );

        Region region = Region.create().
            name( "main" ).
            add( PartComponent.create().
                name( "MyPart" ).
                descriptor( "descriptor-part" ).
                config( myPartConfig ).
                build() ).
            add( ImageComponent.create().
                name( "MyImage" ).
                config( new PropertyTree() ).
                build() ).
            add( LayoutComponent.create().
                name( "MyOtherPart" ).
                descriptor( "descriptor-layout" ).
                config( new PropertyTree() ).

                build() ).
            build();

        /*
        LayoutRegions.newLayoutRegions().add( Region.newRegion().
                    name( "apart" ).
                    add( PartComponent.newPartComponent().
                        name( "PartApart" ).
                        descriptor( "descriptor-part" ).
                        config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
                        build() ).
                    build() ).build()
         */

        PropertyTree pageConfig = new PropertyTree();
        pageConfig.addString( "some", "config" );

        PropertyTree regionAsData = new PropertyTree();

        // exercise
        regionSerializer.toData( region, regionAsData.getRoot() );
        final List<PropertySet> components =
            regionAsData.getProperties( ComponentDataSerializer.COMPONENTS ).stream().map( item -> item.getSet() ).collect(
                Collectors.toList() );

        Region parsedRegion = regionSerializer.fromData( new SerializedData( components.get( 0 ), components ) );

        // verify
        assertEquals( region, parsedRegion );
    }
}