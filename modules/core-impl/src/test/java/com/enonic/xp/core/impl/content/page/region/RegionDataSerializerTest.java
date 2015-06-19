package com.enonic.xp.core.impl.content.page.region;

import org.junit.Test;

import com.enonic.xp.data.PropertyIdProvider;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;

import static org.junit.Assert.*;

public class RegionDataSerializerTest
{
    private RegionDataSerializer regionSerializer = new RegionDataSerializer();

    private PropertyIdProvider propertyIdProvider = new PropertyTree.PredictivePropertyIdProvider();

    @Test
    public void region()
    {
        PropertyTree myPartConfig = new PropertyTree( propertyIdProvider );
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
                config( new PropertyTree( propertyIdProvider ) ).
                build() ).
            add( LayoutComponent.create().
                name( "MyOtherPart" ).
                descriptor( "descriptor-layout" ).
                config( new PropertyTree( propertyIdProvider ) ).

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

        PropertyTree pageConfig = new PropertyTree( propertyIdProvider );
        pageConfig.addString( "some", "config" );

        PropertyTree regionAsData = new PropertyTree( propertyIdProvider );

        // exercise
        regionSerializer.toData( region, regionAsData.getRoot() );
        Region parsedRegion = regionSerializer.fromData( regionAsData.getSet( "region" ) );

        // verify
        assertEquals( region, parsedRegion );
    }
}