package com.enonic.xp.core.impl.export.xml;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.common.io.Resources;

import com.enonic.xp.node.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlNodeParserTest
    extends BaseXmlSerializerTest
{
    @Test
    void testParse()
        throws Exception
    {
        final Node.Builder builder = Node.create();

        final XmlNodeParser parser = new XmlNodeParser();
        parser.source( Resources.asCharSource( getClass().getResource( "node.xml" ), StandardCharsets.UTF_8 ) );
        parser.builder( builder );
        parser.parse();

        final Node node = builder.build();

        final XmlNodeSerializer serializer = new XmlNodeSerializer();
        serializer.node( node ).exportNodeIds( true );
        final String result = serializer.serialize();

        assertXml( "node.xml", result );
    }

    @Test
    void testParse_empty_tags()
        throws Exception
    {
        final Node.Builder builder = Node.create();

        final XmlNodeParser parser = new XmlNodeParser();
        parser.source( Resources.asCharSource( getClass().getResource( "node-empty-tags.xml" ), StandardCharsets.UTF_8 ) );
        parser.builder( builder );
        parser.parse();

        final Node node = builder.build();

        final XmlNodeSerializer serializer = new XmlNodeSerializer();
        serializer.node( node ).exportNodeIds( true );
        final String result = serializer.serialize();

        assertXml( "node.xml", result );
    }

    @Test
    void testParse_valueStartsWithSpace()
    {
        final Node.Builder builder = Node.create();

        final XmlNodeParser parser = new XmlNodeParser();
        parser.source( Resources.asCharSource( getClass().getResource( "node-binary-starts-with-space.xml" ), StandardCharsets.UTF_8 ) );
        parser.builder( builder );
        parser.parse();

        final Node node = builder.build();

        assertEquals( " Picture Name.jpg", node.data().getString( "displayName" ) );
    }
}
