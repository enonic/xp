package com.enonic.xp.portal.impl.resource.render;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.core.content.Content;
import com.enonic.xp.core.content.ContentPath;
import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.Page;
import com.enonic.xp.core.content.page.PageRegions;
import com.enonic.xp.core.content.page.PageTemplate;
import com.enonic.xp.core.content.page.PageTemplateKey;
import com.enonic.xp.core.content.page.region.PartComponent;
import com.enonic.xp.core.content.page.region.Region;
import com.enonic.xp.core.data.PropertyTree;

import static org.junit.Assert.*;

public class EffectivePageResolverTest
{
    private PropertyTree configA;

    private PropertyTree configB;

    private PageRegions regionsA;

    private PageRegions regionsB;

    @Before
    public void before()
    {
        configA = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        configA.addString( "a", "1" );

        configB = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        configB.addString( "b", "1" );

        regionsA = PageRegions.newPageRegions().
            add( Region.newRegion().name( "regionA" ).
                add( PartComponent.newPartComponent().name( "my-part" ).build() ).
                build() ).
            build();

        regionsB = PageRegions.newPageRegions().
            add( Region.newRegion().name( "regionB" ).
                add( PartComponent.newPartComponent().name( "my-part" ).build() ).
                build() ).
            build();
    }

    @Test
    public void given_Content_without_Page_then_effective_Page_is_same_as_in_Template()
    {
        // setup
        PageTemplate template = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "t-1" ) ).
            parentPath( ContentPath.ROOT ).
            name( "my-template" ).
            page( Page.newPage().
                controller( DescriptorKey.from( "my-descriptor" ) ).
                config( configA ).
                regions( regionsA ).
                build() ).
            build();

        Content content = Content.newContent().
            parentPath( ContentPath.ROOT ).
            name( "my-content" ).
            build();

        // exercise
        Page effectivePage = new EffectivePageResolver( content, template ).resolve();

        // verify
        assertEquals( configA, effectivePage.getConfig() );
        assertEquals( regionsA, effectivePage.getRegions() );
        assertEquals( null, effectivePage.getController() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
    }

    @Test
    public void given_Content_with_Page_without_regions_then_effective_Page_gets_regions_from_Template()
    {
        // setup
        PageTemplate template = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "t-1" ) ).
            parentPath( ContentPath.ROOT ).
            name( "my-template" ).
            page( Page.newPage().
                controller( DescriptorKey.from( "my-descriptor" ) ).
                config( configA ).
                regions( regionsA ).
                build() ).
            build();

        Content content = Content.newContent().
            parentPath( ContentPath.ROOT ).
            name( "my-content" ).
            page( Page.newPage().
                template( template.getKey() ).
                config( configB ).
                build() ).
            build();

        // exercise
        Page effectivePage = new EffectivePageResolver( content, template ).resolve();

        // verify
        assertEquals( regionsA, effectivePage.getRegions() );
        assertEquals( configB, effectivePage.getConfig() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertEquals( null, effectivePage.getController() );
    }

    @Test
    public void given_Content_with_Page_without_config_then_effective_Page_gets_config_from_Template()
    {
        // setup
        PageTemplate template = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "t-1" ) ).
            parentPath( ContentPath.ROOT ).
            name( "my-template" ).
            page( Page.newPage().
                controller( DescriptorKey.from( "my-descriptor" ) ).
                config( configA ).
                regions( regionsA ).
                build() ).
            build();

        Content content = Content.newContent().
            parentPath( ContentPath.ROOT ).
            name( "my-content" ).
            page( Page.newPage().
                template( template.getKey() ).
                regions( regionsB ).
                build() ).
            build();

        // exercise
        Page effectivePage = new EffectivePageResolver( content, template ).resolve();

        // verify
        assertEquals( configA, effectivePage.getConfig() );
        assertEquals( regionsB, effectivePage.getRegions() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertEquals( null, effectivePage.getController() );
    }

}