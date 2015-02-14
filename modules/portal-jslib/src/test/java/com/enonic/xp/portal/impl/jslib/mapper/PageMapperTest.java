package com.enonic.xp.portal.impl.jslib.mapper;

import org.junit.Test;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.xp.portal.impl.script.AbstractMapSerializableTest;

public class PageMapperTest
    extends AbstractMapSerializableTest
{
    private final PropertyTree config1;

    private final Region region1;

    public PageMapperTest()
    {
        region1 = Region.newRegion().name( "myRegion" ).build();
        config1 = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        config1.addString( "a", "b" );
    }

    @Test
    public void page_with_emptyRegions()
        throws Exception
    {
        final Page page = Page.newPage().
            controller( DescriptorKey.from( "mymodule:default" ) ).
            config( config1 ).
            regions( PageRegions.newPageRegions().build() ).
            build();

        // exercise
        final PageMapper mapper = new PageMapper( page );

        // verify
        assertJson( "emptyRegions", mapper );
    }

    @Test
    public void page_with_nullRegions()
        throws Exception
    {
        final Page page = Page.newPage().
            controller( DescriptorKey.from( "mymodule:default" ) ).
            config( config1 ).
            regions( null ).
            build();

        // exercise
        final PageMapper mapper = new PageMapper( page );

        // verify
        assertJson( "nullRegions", mapper );
    }


    @Test
    public void page_with_nullConfig()
        throws Exception
    {
        final Page page = Page.newPage().
            controller( DescriptorKey.from( "mymodule:default" ) ).
            config( null ).
            regions( PageRegions.newPageRegions().add( region1 ).build() ).
            build();

        // exercise
        final PageMapper mapper = new PageMapper( page );

        // verify
        assertJson( "nullConfig", mapper );
    }

    @Test
    public void page_with_emptyConfig()
        throws Exception
    {
        final Page page = Page.newPage().
            controller( DescriptorKey.from( "mymodule:default" ) ).
            config( new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() ) ).
            regions( PageRegions.newPageRegions().add( region1 ).build() ).
            build();

        // exercise
        final PageMapper mapper = new PageMapper( page );

        // verify
        assertJson( "emptyConfig", mapper );
    }
}