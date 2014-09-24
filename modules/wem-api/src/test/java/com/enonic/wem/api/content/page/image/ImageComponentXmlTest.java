package com.enonic.wem.api.content.page.image;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.xml.BaseXmlSerializerTest;
import com.enonic.wem.api.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class ImageComponentXmlTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testFrom()
        throws Exception
    {
        RootDataSet componentConfig = new RootDataSet();
        componentConfig.setProperty( "my-prop", Value.newString( "value" ) );
        componentConfig.addProperty( "things", Value.newString( "a" ) );
        componentConfig.addProperty( "things", Value.newString( "b" ) );
        componentConfig.addProperty( "things", Value.newString( "c" ) );

        ImageComponent component = ImageComponent.newImageComponent().
            name( "my-component" ).
            config( componentConfig ).
            build();

        final ImageComponentXml imageComponentXml = new ImageComponentXml();
        imageComponentXml.from( component );
        final String result = XmlSerializers.imageComponent().serialize( imageComponentXml );

        assertXml( "image-component.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "image-component.xml" );
        final ImageComponent.Builder builder = ImageComponent.newImageComponent();

        XmlSerializers.imageComponent().parse( xml ).to( builder );

        final ImageComponent imageComponent = builder.build();

        assertEquals( "my-component", imageComponent.getName().toString() );
        final RootDataSet config = imageComponent.getConfig();
        assertNotNull( config );

        assertEquals( "value", config.getProperty( "my-prop" ).getString() );
        assertEquals( "a", config.getProperty( "things[0]" ).getString() );
        assertEquals( "b", config.getProperty( "things[1]" ).getString() );
        assertEquals( "c", config.getProperty( "things[2]" ).getString() );
    }

}