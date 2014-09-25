package com.enonic.wem.api.content.page.layout;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.xml.BaseXmlSerializerTest;
import com.enonic.wem.api.xml.XmlSerializers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class LayoutComponentXmlTest
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

        LayoutComponent component = LayoutComponent.newLayoutComponent().
            name( "my-component" ).
            descriptor( LayoutDescriptorKey.from( "mainmodule:layoutName" ) ).
            config( componentConfig ).
            build();

        final LayoutComponentXml layoutComponentXml = new LayoutComponentXml();
        layoutComponentXml.from( component );
        final String result = XmlSerializers.layoutComponent().serialize( layoutComponentXml );

        assertXml( "layout-component.xml", result );
    }

    @Test
    public void testTo()
        throws Exception
    {
        final String xml = readFromFile( "layout-component.xml" );
        final LayoutComponent.Builder builder = LayoutComponent.newLayoutComponent();

        XmlSerializers.layoutComponent().parse( xml ).to( builder );

        final LayoutComponent layoutComponent = builder.build();

        assertEquals( "my-component", layoutComponent.getName().toString() );
        final RootDataSet config = layoutComponent.getConfig();
        assertNotNull( config );

        assertEquals( "value", config.getProperty( "my-prop" ).getString() );
        assertEquals( "a", config.getProperty( "things[0]" ).getString() );
        assertEquals( "b", config.getProperty( "things[1]" ).getString() );
        assertEquals( "c", config.getProperty( "things[2]" ).getString() );
    }

}