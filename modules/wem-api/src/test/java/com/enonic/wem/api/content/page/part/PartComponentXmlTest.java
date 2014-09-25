package com.enonic.wem.api.content.page.part;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.xml.BaseXmlSerializerTest;
import com.enonic.wem.api.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class PartComponentXmlTest
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

        PartComponent component = PartComponent.newPartComponent().
            name( "my-component" ).
            descriptor( PartDescriptorKey.from( "mainmodule:partName" ) ).
            config( componentConfig ).
            build();

        final PartComponentXml partComponentXml = new PartComponentXml();
        partComponentXml.from( component );
        final String result = XmlSerializers.partComponent().serialize( partComponentXml );

        assertXml( "part-component.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "part-component.xml" );
        final PartComponent.Builder builder = PartComponent.newPartComponent();

        XmlSerializers.partComponent().parse( xml ).to( builder );

        final PartComponent partComponent = builder.build();

        assertEquals( "my-component", partComponent.getName().toString() );
        final RootDataSet config = partComponent.getConfig();
        assertNotNull( config );

        assertEquals( "value", config.getProperty( "my-prop" ).getString() );
        assertEquals( "a", config.getProperty( "things[0]" ).getString() );
        assertEquals( "b", config.getProperty( "things[1]" ).getString() );
        assertEquals( "c", config.getProperty( "things[2]" ).getString() );
    }

}