package com.enonic.xp.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.support.XmlTestHelper;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class DateTimeTypeTest
{
    private final static ApplicationKey CURRENT_APPLICATION = ApplicationKey.from( "myapplication" );

    private XmlTestHelper xmlHelper;

    private JsonTestHelper jsonHelper;

    private DateTimeType serializer = new DateTimeType();

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
        DateTimeTypeConfig.Builder builder = DateTimeTypeConfig.create();
        builder.withTimezone( true );
        DateTimeTypeConfig expected = builder.build();

        // exercise
        DateTimeTypeConfig parsed =
            (DateTimeTypeConfig) serializer.parseConfig( CURRENT_APPLICATION, xmlHelper.parseXml( "parseConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void parseConfig_timezone_as_empty()
        throws IOException
    {
        // setup
        DateTimeTypeConfig.Builder builder = DateTimeTypeConfig.create();
        DateTimeTypeConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<with-timezone></with-timezone>" );
        xml.append( "</config>\n" );

        // exercise
        DateTimeTypeConfig parsed =
            (DateTimeTypeConfig) serializer.parseConfig( CURRENT_APPLICATION, DomHelper.parse( xml.toString() ).getDocumentElement() );

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
        DateTimeTypeConfig expected = DateTimeTypeConfig.create().build();

        // exercise
        DateTimeTypeConfig parsed =
            (DateTimeTypeConfig) serializer.parseConfig( CURRENT_APPLICATION, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        DateTimeTypeConfig.Builder builder = DateTimeTypeConfig.create();
        builder.withTimezone( true );
        DateTimeTypeConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config );

        // verify
        this.jsonHelper.assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }
}
