package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class PageTemplateXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        RootDataSet partInHeaderConfig = new RootDataSet();
        partInHeaderConfig.setProperty( "width", new Value.Long( 500 ) );
        partInHeaderConfig.setProperty( "caption", new Value.String( "So sweet!" ) );

        Region region = Region.newRegion().
            name( "my-region" ).
            add( PartComponent.newPartComponent().
                name( "PartInHeader" ).
                template( "demo|my-part-template" ).
                config( partInHeaderConfig ).
                build() ).
            build();
        PageRegions regions = PageRegions.newPageRegions().add( region ).build();

        RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );
        pageTemplateConfig.addProperty( "thing.first", new Value.String( "one" ) );
        pageTemplateConfig.addProperty( "thing.second", new Value.String( "two" ) );

        PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "mainmodule|main-page" ) ).
            displayName( "Main page template" ).
            regions( regions ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "com.enonic.sometype", "some.other.type" ) ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mainmodule-1.0.0" ), new ComponentDescriptorName( "landing-page" ) ) ).
            build();

        final PageTemplateXml pageTemplateXml = new PageTemplateXml();
        pageTemplateXml.from( pageTemplate );
        final String result = XmlSerializers.pageTemplate().serialize( pageTemplateXml );

        assertXml( "page-template.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "page-template.xml" );
        final PageTemplate.Builder builder = PageTemplate.newPageTemplate();
        builder.name( "my-page-template" );
        XmlSerializers.pageTemplate().parse( xml ).to( builder );

        builder.module( ModuleName.from( "demo" ) );
        final PageTemplate pageTemplate = builder.build();

        assertEquals( "Main page template", pageTemplate.getDisplayName() );
        assertEquals( PageDescriptorKey.from( "mainmodule-1.0.0:landing-page" ), pageTemplate.getDescriptor() );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "com.enonic.sometype" ) ) );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "some.other.type" ) ) );

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
        assertEquals( "demo|my-part-template", partComponent.getTemplate().toString() );

        // verify: component config
        RootDataSet partComponentConfig = partComponent.getConfig();
        assertEquals( new Long( 500 ), partComponentConfig.getProperty( "width" ).getLong() );
        assertEquals( "So sweet!", partComponentConfig.getProperty( "caption" ).getString() );
    }
}
