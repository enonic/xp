package com.enonic.xp.portal.impl.handler.render;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EffectivePageResolverTest
{
    private PropertyTree configA;

    private PropertyTree configB;

    private PageRegions regionsA;

    private PageRegions regionsB;

    @BeforeEach
    public void before()
    {
        configA = new PropertyTree();
        configA.addString( "a", "1" );

        configB = new PropertyTree();
        configB.addString( "b", "1" );

        regionsA = PageRegions.create().
            add( Region.create().name( "regionA" ).
                add( PartComponent.create().descriptor( "myapp:my-part" ).build() ).
                build() ).
            build();

        regionsB = PageRegions.create().
            add( Region.create().name( "regionB" ).
                add( PartComponent.create().descriptor( "myapp:my-part" ).build() ).
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
            page( Page.create().
                descriptor( DescriptorKey.from( "my-descriptor" ) ).
                config( configA ).
                regions( regionsA ).
                build() ).
            build();

        Content content = Content.create().
            parentPath( ContentPath.ROOT ).
            name( "my-content" ).
            build();

        // exercise
        Page effectivePage = new EffectivePageResolver( content, template ).resolve();

        // verify
        assertEquals( configA, effectivePage.getConfig() );
        assertEquals( regionsA, effectivePage.getRegions() );
        assertEquals( null, effectivePage.getDescriptor() );
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
            page( Page.create().
                descriptor( DescriptorKey.from( "my-descriptor" ) ).
                config( configA ).
                regions( regionsA ).
                build() ).
            build();

        Content content = Content.create().
            parentPath( ContentPath.ROOT ).
            name( "my-content" ).
            page( Page.create().
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
        assertEquals( null, effectivePage.getDescriptor() );
    }

    @Test
    public void given_Content_with_Page_without_config_then_effective_Page_gets_config_from_Template()
    {
        // setup
        PageTemplate template = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "t-1" ) ).
            parentPath( ContentPath.ROOT ).
            name( "my-template" ).
            page( Page.create().
                descriptor( DescriptorKey.from( "my-descriptor" ) ).
                config( configA ).
                regions( regionsA ).
                build() ).
            build();

        Content content = Content.create().
            parentPath( ContentPath.ROOT ).
            name( "my-content" ).
            page( Page.create().
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
        assertEquals( null, effectivePage.getDescriptor() );
    }

    //
}
