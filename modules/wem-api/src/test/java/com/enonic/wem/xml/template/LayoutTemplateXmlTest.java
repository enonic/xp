package com.enonic.wem.xml.template;

import org.junit.Test;

import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.LayoutTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;

public class LayoutTemplateXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        LayoutTemplate layoutTemplate = LayoutTemplate.newLayoutTemplate().
            name( new LayoutTemplateName( "layout-name" ) ).
            displayName( "Layout template" ).
            config( layoutTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/layout-temp.xml" ) ).
            build();

        final LayoutTemplateXml layoutTemplateXml = new LayoutTemplateXml();
        layoutTemplateXml.from( layoutTemplate );
        final String result = XmlSerializers.layoutTemplate().serialize( layoutTemplateXml );

        assertXml( "layout-template.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "layout-template.xml" );
        final LayoutTemplate.Builder builder = LayoutTemplate.newLayoutTemplate();

        XmlSerializers.layoutTemplate().parse( xml ).to( builder );

        final LayoutTemplate layoutTemplate = builder.build();

        assertEquals( "Layout template", layoutTemplate.getDisplayName() );
        assertEquals( ModuleResourceKey.from( "mainmodule-1.0.0:/components/layout-temp.xml" ), layoutTemplate.getDescriptor() );

        assertEquals( 200L, layoutTemplate.getConfig().getProperty( "width" ).getLong().longValue() );
    }
}
