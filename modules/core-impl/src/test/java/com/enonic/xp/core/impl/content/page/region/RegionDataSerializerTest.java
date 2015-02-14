package com.enonic.xp.core.impl.content.page.region;

import org.junit.Test;

import com.enonic.xp.core.content.page.region.ImageComponent;
import com.enonic.xp.core.content.page.region.LayoutComponent;
import com.enonic.xp.core.content.page.region.PartComponent;
import com.enonic.xp.core.content.page.region.Region;
import com.enonic.xp.core.data.PropertyIdProvider;
import com.enonic.xp.core.data.PropertyTree;

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