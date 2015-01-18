package com.enonic.wem.api.content.page.region;

import org.junit.Test;

import com.enonic.wem.api.data.PropertyIdProvider;
import com.enonic.wem.api.data.PropertyTree;

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

        Region region = Region.newRegion().
            name( "main" ).
            add( PartComponent.newPartComponent().
                name( "MyPart" ).
                descriptor( "descriptor-part" ).
                config( myPartConfig ).
                build() ).
            add( ImageComponent.newImageComponent().
                name( "MyImage" ).
                config( new PropertyTree( propertyIdProvider ) ).
                build() ).
            add( LayoutComponent.newLayoutComponent().
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