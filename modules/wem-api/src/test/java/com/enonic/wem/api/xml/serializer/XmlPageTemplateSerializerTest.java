package com.enonic.wem.api.xml.serializer;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.xml.mapper.XmlPageTemplateMapper;
import com.enonic.wem.api.xml.model.XmlPageTemplate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class XmlPageTemplateSerializerTest
    extends BaseXmlSerializer2Test
{
    @Test
    public void test_to_xml()
        throws Exception
    {
        RootDataSet partInHeaderConfig = new RootDataSet();
        partInHeaderConfig.setProperty( "width", Value.newLong( 500 ) );
        partInHeaderConfig.setProperty( "caption", Value.newString( "So sweet!" ) );

        Region region = Region.newRegion().
            name( "my-region" ).
            add( PartComponent.newPartComponent().
                name( "PartInHeader" ).
                descriptor( "demo:my-part-template" ).
                config( partInHeaderConfig ).
                build() ).
            build();
        PageRegions regions = PageRegions.newPageRegions().add( region ).build();

        RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", Value.newLong( 10000 ) );
        pageTemplateConfig.addProperty( "thing.first", Value.newString( "one" ) );
        pageTemplateConfig.addProperty( "thing.second", Value.newString( "two" ) );

        PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "main-page" ) ).
            displayName( "Main page template" ).
            regions( regions ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "mymodule:com.enonic.sometype", "mymodule:some.other.type" ) ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mainmodule" ), new ComponentDescriptorName( "landing-page" ) ) ).
            build();

        XmlPageTemplate xmlObject = XmlPageTemplateMapper.toXml( pageTemplate );
        String result = XmlSerializers2.pageTemplate().serialize( xmlObject );

        assertXml( "page-template.xml", result );
    }

    @Test
    public void test_templates_sort_by_displayName2()
        throws Exception
    {
        // 1
        final String xml = readFromFile( "page-template.xml" );
        XmlPageTemplate xmlObject = XmlSerializers2.pageTemplate().parse( xml );
        PageTemplate.Builder builder = PageTemplate.newPageTemplate();
        builder.name( "my-page-template1" );
        XmlPageTemplateMapper.fromXml( xmlObject, builder );

        builder.displayName( "BBB" );
        PageTemplate pageTemplate = builder.build();

        final PageTemplates.Builder player = PageTemplates.newPageTemplates();
        player.add( pageTemplate );

        // 2
        builder = PageTemplate.newPageTemplate();
        builder.name( "my-page-template2" );
        XmlPageTemplateMapper.fromXml( xmlObject, builder );

        builder.displayName( "CCC" );
        pageTemplate = builder.build();
        player.add( pageTemplate );

        // 3
        builder = PageTemplate.newPageTemplate();
        builder.name( "my-page-template3" );
        XmlPageTemplateMapper.fromXml( xmlObject, builder );

        builder.displayName( "AAA" );
        pageTemplate = builder.build();
        player.add( pageTemplate );

        PageTemplates sorted = player.build();
        List<String> result = templatesAsString( sorted.getList() );

        assertEquals( "AAA, BBB, CCC", Joiner.on( ", " ).join( result ) );
    }

    private List<String> templatesAsString( final List<PageTemplate> templateList )
    {
        return Lists.transform( templateList, PageTemplate::getDisplayName );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "page-template.xml" );
        final PageTemplate.Builder builder = PageTemplate.newPageTemplate();
        builder.name( "my-page-template" );

        XmlPageTemplate xmlObject = XmlSerializers2.pageTemplate().parse( xml );
        XmlPageTemplateMapper.fromXml( xmlObject, builder );
        PageTemplate pageTemplate = builder.build();

        assertEquals( "Main page template", pageTemplate.getDisplayName() );
        assertEquals( PageDescriptorKey.from( "mainmodule:landing-page" ), pageTemplate.getDescriptor() );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "mymodule:com.enonic.sometype" ) ) );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "mymodule:some.other.type" ) ) );

        // verify: config
        RootDataSet config = pageTemplate.getConfig();
        assertEquals( 10000L, config.getProperty( "pause" ).getLong().longValue() );
        assertEquals( "one", config.getProperty( "thing.first" ).getString() );
        assertEquals( "two", config.getProperty( "thing.second" ).getString() );

        // verify: regions
        PageRegions regions = pageTemplate.getRegions();
        Region region = regions.getRegion( "my-region" );
        assertNotNull( region );
        assertEquals( "my-region", region.getName() );

        // verify: components in region
        assertEquals( 1, region.numberOfComponents() );

        // verify: part component
        PageComponent component = region.getComponents().iterator().next();
        assertTrue( component instanceof PartComponent );
        PartComponent partComponent = (PartComponent) component;
        assertEquals( "PartInHeader", partComponent.getName().toString() );
        assertEquals( "demo:my-part-template", partComponent.getDescriptor().toString() );

        // verify: component config
        RootDataSet partComponentConfig = partComponent.getConfig();
        assertEquals( new Long( 500 ), partComponentConfig.getProperty( "width" ).getLong() );
        assertEquals( "So sweet!", partComponentConfig.getProperty( "caption" ).getString() );
    }

}
