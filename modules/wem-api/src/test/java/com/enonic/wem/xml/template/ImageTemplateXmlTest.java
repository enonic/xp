package com.enonic.wem.xml.template;

import org.junit.Test;

import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.xml.BaseXmlSerializerTest;
import com.enonic.wem.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;

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
            name( new ImageTemplateName( "image" ) ).
            displayName( "Image template" ).
            config( imageTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/image-temp.xml" ) ).
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
        assertEquals( ModuleResourceKey.from( "mainmodule-1.0.0:/components/image-temp.xml" ), imageTemplate.getDescriptor() );

        assertEquals( 200L, imageTemplate.getConfig().getProperty( "width" ).getLong().longValue() );
    }
}
