package com.enonic.wem.api.xml;

import org.junit.Test;

import static org.junit.Assert.*;

public class DomBuilderTest
{
    @Test
    public void buildDocument()
    {
        final DomBuilder builder = DomBuilder.create( "items" );
        builder.start( "item" ).attribute( "id", "1" ).attribute( "show", "false" ).end();
        builder.start( "item" ).attribute( "id", "2" ).attribute( "show", "true" ).end();

        final String str = DomHelper.serialize( builder.getDocument() );
        assertEquals( "<items>\n" +
                          "    <item id=\"1\" show=\"false\"/>\n" +
                          "    <item id=\"2\" show=\"true\"/>\n" +
                          "</items>", str.trim() );
    }
}
