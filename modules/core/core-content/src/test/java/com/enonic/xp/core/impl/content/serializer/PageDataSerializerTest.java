package com.enonic.xp.core.impl.content.serializer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.core.impl.content.page.AbstractDataSerializerTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.TextComponent;

import static org.junit.Assert.*;

public class PageDataSerializerTest
    extends AbstractDataSerializerTest
{
    private PageDataSerializer pageDataSerializer;

    private PageDescriptorService pageDescriptorService;

    @Before
    public void setUp()
    {
        super.setUp();

        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );

        this.pageDataSerializer = PageDataSerializer.create().
            pageDescriptorService( pageDescriptorService ).
            partDescriptorService( partDescriptorService ).
            layoutDescriptorService( layoutDescriptorService ).
            contentService( contentService ).
            build();
    }

    @Test
    public void page()
    {
        final PropertyTree myPartConfig = new PropertyTree();
        myPartConfig.addString( "some", "config" );
        final PropertyTree imageConfig = new PropertyTree();
        imageConfig.addString( "caption", "Caption" );
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "app-key:d-name" );
        final String regionName1 = "top";
        final String regionName2 = "bottom";

        Region mainRegion1 = Region.create().
            name( regionName1 ).
            add( createPartComponent( "MyPart1", "app-descriptor-x:name-x", myPartConfig ) ).
            add( createLayoutComponent() ).
            add( LayoutComponent.create().name( "Layout" ).build() ).
            build();

        Region mainRegion2 = Region.create().
            name( regionName2 ).
            add( createPartComponent( "MyPart2", "app-descriptor-y:name-y", myPartConfig ) ).
            add( createImageComponent( "img-id-x", "Image Component", imageConfig ) ).
            add( ImageComponent.create().name( "Image" ).build() ).
            build();

        PageRegions regions = PageRegions.create().
            add( mainRegion1 ).
            add( mainRegion2 ).
            build();

        PropertyTree pageConfig = new PropertyTree();
        pageConfig.addString( "some", "config" );
        pageConfig.addDouble( "aim", 42.0 );

        Page page = Page.create().
            config( pageConfig ).
            descriptor( pageDescriptorKey ).
            regions( regions ).
            build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( PageDescriptor.create().
            config( Form.create().build() ).
            key( pageDescriptorKey ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( regionName1 ).build() ).
                add( RegionDescriptor.create().name( regionName2 ).build() ).
                build() ).
            build() );

        PropertyTree pageAsData = new PropertyTree();
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getRoot() );

        // verify
        assertEquals( page, parsedPage );
    }

    @Test
    public void page_template()
    {
        Page page = Page.create().
            template( PageTemplateKey.from( "template-id" ) ).
            build();

        PropertyTree pageAsData = new PropertyTree();
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getRoot() );

        // verify
        assertEquals( page, parsedPage );
    }

    @Test
    public void page_with_regions_as_null()
    {
        final PropertyTree pageConfig = new PropertyTree();
        pageConfig.addString( "some", "config" );
        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "app-key:d-name" );

        final Page page = Page.create().
            config( pageConfig ).
            descriptor( pageDescriptorKey ).
            regions( null ).
            build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) ).thenReturn( PageDescriptor.create().
            config( Form.create().build() ).
            key( pageDescriptorKey ).
            regions( RegionDescriptors.create().build() ).
            build() );

        final PropertyTree pageAsData = new PropertyTree();
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        final Page parsedPage = pageDataSerializer.fromData( pageAsData.getRoot() );

        // verify
        assertEquals( page, parsedPage );
    }


    @Test
    public void fragmentPage()
    {
        Page page = Page.create().
            fragment( createLayoutComponent() ).
            build();

        PropertyTree pageAsData = new PropertyTree();
        pageDataSerializer.toData( page, pageAsData.getRoot() );
        Page parsedPage = pageDataSerializer.fromData( pageAsData.getRoot() );

        // verify
        assertEquals( page, parsedPage );
    }

    private LayoutComponent createLayoutComponent()
    {
        final String layoutName = "MyLayout";
        final DescriptorKey layoutDescriptorKey = DescriptorKey.from( "layoutDescriptor:name" );
        final String regionName1 = "left";
        final String regionName2 = "right";

        final Region region1 = Region.create().
            name( regionName1 ).
            add( PartComponent.create().
                name( "Part" ).
                build() ).
            add( TextComponent.create().
                name( "Text" ).
                text( "text text text" ).
                build() ).
            add( TextComponent.create().
                name( "Text" ).
                build() ).
            build();

        final Region region2 = Region.create().
            name( regionName2 ).
            add( createImageComponent( "image-id", "Some Image", null ) ).
            add( createFragmentComponent( "213sda-ss222", "My Fragment" ) ).
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

        return LayoutComponent.create().name( layoutName ).descriptor( layoutDescriptorKey ).regions( layoutRegions ).build();
    }
}