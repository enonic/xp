package com.enonic.wem.xml.template;

import org.junit.Test;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;

public class PageTemplateXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );
//        pageTemplateConfig.addProperty( "first", new Value.String( "one" ));
//        pageTemplateConfig.addProperty( "second", new Value.String( "two" ));

        PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            name( new PageTemplateName( "main-page" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/landing-page.xml" ) ).
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

        XmlSerializers.pageTemplate().parse( xml ).to( builder );

        final PageTemplate pageTemplate = builder.build();

        assertEquals( "Main page template", pageTemplate.getDisplayName() );
        assertEquals( ModuleResourceKey.from( "mainmodule-1.0.0:/components/landing-page.xml" ), pageTemplate.getDescriptor() );

        assertEquals( 10000L, pageTemplate.getConfig().getProperty( "pause" ).getLong().longValue() );
    }
}
