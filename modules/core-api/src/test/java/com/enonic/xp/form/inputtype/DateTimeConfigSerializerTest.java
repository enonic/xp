package com.enonic.xp.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.support.XmlTestHelper;
import com.enonic.xp.xml.DomHelper;

import static com.enonic.xp.support.JsonTestHelper.assertJsonEquals;
import static org.junit.Assert.*;

public class DateTimeConfigSerializerTest
{
    private final static ApplicationKey CURRENT_MODULE = ApplicationKey.from( "mymodule" );

    private XmlTestHelper xmlHelper;

    private JsonTestHelper jsonHelper;

    private DateTimeConfigSerializer serializer = new DateTimeConfigSerializer();

    @Before
    public void before()
    {
        xmlHelper = new XmlTestHelper( this );
        jsonHelper = new JsonTestHelper( this );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        DateTimeConfig.Builder builder = DateTimeConfig.create();
        builder.withTimezone( true );
        DateTimeConfig expected = builder.build();

        // exercise
        DateTimeConfig parsed = serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml( "parseConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void parseConfig_timezone_as_empty()
        throws IOException
    {
        // setup
        DateTimeConfig.Builder builder = DateTimeConfig.create();
        DateTimeConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<with-timezone></with-timezone>" );
        xml.append( "</config>\n" );

        // exercise
        DateTimeConfig parsed = serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void parseConfig_timezone_not_specified()
        throws IOException
    {
        // setup
        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "</config>\n" );
        DateTimeConfig expected = DateTimeConfig.create().build();

        // exercise
        DateTimeConfig parsed = serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        DateTimeConfig.Builder builder = DateTimeConfig.create();
        builder.withTimezone( true );
        DateTimeConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config );

        // verify
        assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }
}
