package com.enonic.xp.core.impl.export.xml.serializer;

import org.junit.Test;

import com.enonic.xp.node.Node;

public class XmlNodeParserTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testParse()
        throws Exception
    {
        final Node.Builder builder = Node.newNode();

        final XmlNodeParser parser = new XmlNodeParser();
        parser.source( getClass().getResource( "node2.xml" ) );
        parser.builder( builder );
        parser.parse();

        final Node node = builder.build();

        final XmlNodeSerializer2 serializer = new XmlNodeSerializer2();
        serializer.node( node ).exportNodeIds( true );
        final String result = serializer.serialize();

        assertXml( "node2.xml", result );
    }
}
