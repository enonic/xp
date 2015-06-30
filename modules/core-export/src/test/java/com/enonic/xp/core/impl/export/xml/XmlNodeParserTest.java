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

    @Test
    // Handles the double-encoded-case that happens with old 5.0.1 exports.
    public void testParse_double_decoded()
        throws Exception
    {
        final Node.Builder builder = Node.newNode();

        final XmlNodeParser parser = new XmlNodeParser();
        parser.source( getClass().getResource( "node-double-encoded.xml" ) );
        parser.builder( builder );
        parser.parse();

        final Node node = builder.build();

        final XmlNodeSerializer serializer = new XmlNodeSerializer();
        serializer.node( node ).exportNodeIds( true );
        final String result = serializer.serialize();

        assertXml( "node.xml", result );
    }


    @Test
    public void testParse_empty_tags()
        throws Exception
    {
        final Node.Builder builder = Node.newNode();

        final XmlNodeParser parser = new XmlNodeParser();
        parser.source( getClass().getResource( "node-empty-tags.xml" ) );
        parser.builder( builder );
        parser.parse();

        final Node node = builder.build();

        final XmlNodeSerializer serializer = new XmlNodeSerializer();
        serializer.node( node ).exportNodeIds( true );
        final String result = serializer.serialize();

        assertXml( "node.xml", result );
    }
}
