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

public class DateTypeTest
{
    private final static ApplicationKey CURRENT_APPLICATION = ApplicationKey.from( "myapplication" );

    private XmlTestHelper xmlHelper;

    private JsonTestHelper jsonHelper;

    private DateType serializer = new DateType();

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
        DateTypeConfig.Builder builder = DateTypeConfig.create();
        builder.withTimezone( true );
        DateTypeConfig expected = builder.build();

        // exercise
        DateTypeConfig parsed =
            (DateTypeConfig) serializer.parseConfig( CURRENT_APPLICATION, xmlHelper.parseXml( "parseConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void parseConfig_timezone_as_empty()
        throws IOException
    {
        // setup
        DateTypeConfig.Builder builder = DateTypeConfig.create();
        DateTypeConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<with-timezone></with-timezone>" );
        xml.append( "</config>\n" );

        // exercise
        DateTypeConfig parsed =
            (DateTypeConfig) serializer.parseConfig( CURRENT_APPLICATION, DomHelper.parse( xml.toString() ).getDocumentElement() );

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
        DateTypeConfig expected = DateTypeConfig.create().build();

        // exercise
        DateTypeConfig parsed =
            (DateTypeConfig) serializer.parseConfig( CURRENT_APPLICATION, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        DateTypeConfig.Builder builder = DateTypeConfig.create();
        builder.withTimezone( true );
        DateTypeConfig config = builder.build();

        // exercise
        JsonNode json = serializer.serializeConfig( config );

        // verify
        this.jsonHelper.assertJsonEquals( jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }
}
