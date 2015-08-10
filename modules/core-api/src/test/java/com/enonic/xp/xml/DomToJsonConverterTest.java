package com.enonic.xp.xml;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.support.XmlTestHelper;

public class DomToJsonConverterTest
{
    private XmlTestHelper xmlTestHelper;

    private JsonTestHelper jsonTestHelper;

    @Before
    public void setup()
    {
        this.xmlTestHelper = new XmlTestHelper( this );
        this.jsonTestHelper = new JsonTestHelper( this );
    }

    private Element parseXml( final String name )
    {
        return this.xmlTestHelper.parseXml( name ).getDocumentElement();
    }

    private JsonNode parseJson( final String name )
    {
        return this.jsonTestHelper.loadTestJson( name );
    }

    @Test
    public void convertSimple()
    {
        final Element xml = parseXml( "simple.xml" );
        final JsonNode expectedJson = parseJson( "simple.json" );

        final ObjectNode json = new DomToJsonConverter().convert( xml );
        this.jsonTestHelper.assertJsonEquals( expectedJson, json );
    }

    @Test
    public void convertComplex()
    {
        final Element xml = parseXml( "complex.xml" );
        final JsonNode expectedJson = parseJson( "complex.json" );

        final ObjectNode json = new DomToJsonConverter().convert( xml );
        this.jsonTestHelper.assertJsonEquals( expectedJson, json );
    }
}
