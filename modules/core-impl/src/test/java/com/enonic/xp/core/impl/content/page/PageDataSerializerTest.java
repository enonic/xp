package com.enonic.xp.core.impl.content.page;

import org.junit.Test;

import com.enonic.xp.data.PropertyIdProvider;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;

import static org.junit.Assert.*;

public class PageDataSerializerTest
{
    private PropertyIdProvider propertyIdProvider = new PropertyTree.PredictivePropertyIdProvider();

    private PageDataSerializer pageDataSerializer = new PageDataSerializer( "page" );

    @Test
    public void page()
    {
        PropertyTree myPartConfig = new PropertyTree( propertyIdProvider );
        myPartConfig.addString( "some", "config" );

        Region mainRegion = Region.newRegion().
            name( "main" ).
            add( PartComponent.newPartComponent().
                name( "MyPart" ).
                descriptor( "descriptor-x" ).
                config( myPartConfig ).
                build() ).
            build();

        PageRegions regions = PageRegions.newPageRegions().
            add( mainRegion ).
            build();

        PropertyTree pageConfig = new PropertyTree( propertyIdProvider );
        pageConfig.addString( "some", "config" );

        Page page = Page.newPage().
            config( pageConfig ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( regions ).
            build();

        PropertyTree pageAsData = new PropertyTree( propertyIdProvider );
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getSet( "page" ) );

        // verify
        assertEquals( page, parsedPage );
    }

    @Test
    public void page_with_empty_regions()
    {
        PageRegions regions = PageRegions.newPageRegions().
            build();

        PropertyTree pageConfig = new PropertyTree( propertyIdProvider );
        pageConfig.addString( "some", "config" );

        Page page = Page.newPage().
            config( pageConfig ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( regions ).
            build();

        PropertyTree pageAsData = new PropertyTree( propertyIdProvider );
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getSet( "page" ) );

        // verify
        assertEquals( page, parsedPage );
    }

    @Test
    public void page_with_regions_as_null()
    {
        PropertyTree pageConfig = new PropertyTree( propertyIdProvider );
        pageConfig.addString( "some", "config" );

        Page page = Page.newPage().
            config( pageConfig ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( null ).
            build();

        PropertyTree pageAsData = new PropertyTree( propertyIdProvider );
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getSet( "page" ) );

        // verify
        assertEquals( page, parsedPage );
    }
}