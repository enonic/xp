package com.enonic.wem.api.content.page.part;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;

@Ignore
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
            key( PartTemplateKey.from( "mainmodule|news-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( PartDescriptorKey.from( "mainmodule-1.0.0:news-part" ) ).
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
        assertEquals( PartDescriptorKey.from( "mainmodule-1.0.0:news-part" ), partTemplate.getDescriptor() );

        assertEquals( 200L, partTemplate.getConfig().getProperty( "width" ).getLong().longValue() );
    }
}
