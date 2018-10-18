package com.enonic.xp.core.impl.content.page;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.content.serializer.PageDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;

import static org.junit.Assert.*;

public class PageDataSerializerTest
{

    private PageDataSerializer pageDataSerializer = new PageDataSerializer( "page" );

    @Test
    public void page()
    {
        PropertyTree myPartConfig = new PropertyTree();
        myPartConfig.addString( "some", "config" );

        Region mainRegion1 = Region.create().
            name( "top" ).
            add( PartComponent.create().
                name( "MyPart1" ).
                descriptor( "descriptor-x" ).
                config( myPartConfig ).
                build() ).
            add( createLayoutComponent() ).
            build();

        Region mainRegion2 = Region.create().
            name( "bottom" ).
            add( PartComponent.create().
                name( "MyPart2" ).
                descriptor( "descriptor-y" ).
                config( myPartConfig ).
                build() ).
            build();

        PageRegions regions = PageRegions.create().
            add( mainRegion1 ).
            add( mainRegion2 ).
            build();

        PropertyTree pageConfig = new PropertyTree();
        pageConfig.addString( "some", "config" );

        Page page = Page.create().
            config( pageConfig ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( regions ).
            build();

        PropertyTree pageAsData = new PropertyTree();
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getSet( "page" ) );

        // verify
        assertEquals( page, parsedPage );
    }

    @Test
    public void page_with_empty_regions()
    {
        PageRegions regions = PageRegions.create().
            build();

        PropertyTree pageConfig = new PropertyTree();
        pageConfig.addString( "some", "config" );

        Page page = Page.create().
            config( pageConfig ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( regions ).
            build();

        PropertyTree pageAsData = new PropertyTree();
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getSet( "page" ) );

        // verify
        assertEquals( page, parsedPage );
    }

    @Test
    public void page_with_regions_as_null()
    {
        PropertyTree pageConfig = new PropertyTree();
        pageConfig.addString( "some", "config" );

        Page page = Page.create().
            config( pageConfig ).
            template( PageTemplateKey.from( "template-x" ) ).
            regions( null ).
            build();

        PropertyTree pageAsData = new PropertyTree();
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getSet( "page" ) );

        // verify
        assertEquals( page, parsedPage );
    }


    @Test
    public void fragmentPage()
    {
        PropertyTree myPartConfig = new PropertyTree();
        myPartConfig.addString( "some", "config" );

        PropertyTree pageConfig = new PropertyTree();

        Page page = Page.create().
            config( pageConfig ).
            fragment( createLayoutComponent() ).
            build();

        PropertyTree pageAsData = new PropertyTree();
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getSet( "page" ) );

        // verify
        assertEquals( page, parsedPage );
    }

    private LayoutComponent createLayoutComponent()
    {
        final Region region1 = Region.create().
            name( "left" ).
            add( PartComponent.create().
                name( "Some Part 1 " ).
                build() ).
            add( PartComponent.create().
                name( "Some Part 2 " ).
                build() ).
            build();

        final Region region2 = Region.create().
            name( "right" ).
            add( PartComponent.create().
                name( "Some Part 3 " ).
                build() ).
            add( FragmentComponent.create().name( "My Fragment" ).fragment( ContentId.from( "213sda-ss222" ) ).build() ).
            build();

        final LayoutRegions layoutRegions = LayoutRegions.create().add( region1 ).add( region2 ).build();

        return LayoutComponent.create().name( "MyLayout" ).descriptor( "layoutDescriptor" ).regions( layoutRegions ).build();
    }
}