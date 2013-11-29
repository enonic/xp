package com.enonic.wem.xml.template;

import org.junit.Test;

import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;

public class PartTemplateXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        PartTemplate partTemplate = PartTemplate.newPartTemplate().
            name( new PartTemplateName( "news-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/news-part.xml" ) ).
            build();

        final PartTemplateXml partTemplateXml = new PartTemplateXml();
        partTemplateXml.from( partTemplate );
        final String result = XmlSerializers.partTemplate().serialize( partTemplateXml );

        assertXml( "part-template.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "part-template.xml" );
        final PartTemplate.Builder builder = PartTemplate.newPartTemplate();

        XmlSerializers.partTemplate().parse( xml ).to( builder );

        final PartTemplate partTemplate = builder.build();

        assertEquals( "News part template", partTemplate.getDisplayName() );
        assertEquals( ModuleResourceKey.from( "mainmodule-1.0.0:/components/news-part.xml" ), partTemplate.getDescriptor() );

        assertEquals( 200L, partTemplate.getConfig().getProperty( "width" ).getLong().longValue() );
    }
}
