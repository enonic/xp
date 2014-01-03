package com.enonic.wem.api.content.page.image;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;

@Ignore
public class ImageTemplateXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        RootDataSet imageTemplateConfig = new RootDataSet();
        imageTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        ImageTemplate imageTemplate = ImageTemplate.newImageTemplate().
            key( ImageTemplateKey.from( "sitetemplate-1.0.0|mainmodule-1.0.0|image" ) ).
            displayName( "Image template" ).
            config( imageTemplateConfig ).
            descriptor( ImageDescriptorKey.from( "mainmodule-1.0.0:image-temp" ) ).
            build();

        final ImageTemplateXml imageTemplateXml = new ImageTemplateXml();
        imageTemplateXml.from( imageTemplate );
        final String result = XmlSerializers.imageTemplate().serialize( imageTemplateXml );

        assertXml( "image-template.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "image-template.xml" );
        final ImageTemplate.Builder builder = ImageTemplate.newImageTemplate();

        XmlSerializers.imageTemplate().parse( xml ).to( builder );

        final ImageTemplate imageTemplate = builder.build();

        assertEquals( "Image template", imageTemplate.getDisplayName() );
        assertEquals( ImageDescriptorKey.from( "mainmodule-1.0.0:image-temp" ), imageTemplate.getDescriptor() );

        assertEquals( 200L, imageTemplate.getConfig().getProperty( "width" ).getLong().longValue() );
    }
}
