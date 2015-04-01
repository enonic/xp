package com.enonic.xp.core.impl.export.xml;

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
        parser.source( getClass().getResource( "node.xml" ) );
        parser.builder( builder );
        parser.parse();

        final Node node = builder.build();

        final XmlNodeSerializer serializer = new XmlNodeSerializer();
        serializer.node( node ).exportNodeIds( true );
        final String result = serializer.serialize();

        assertXml( "node.xml", result );
    }
}
