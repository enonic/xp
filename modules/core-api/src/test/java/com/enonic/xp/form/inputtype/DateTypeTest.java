package com.enonic.xp.form.inputtype;


import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.support.XmlTestHelper;

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

    private InputTypeConfig parse( final String name )
    {
        // setup
        DateTypeConfig.Builder builder = DateTypeConfig.create();
        builder.withTimezone( true );
        DateTypeConfig expected = builder.build();

        // exercise
        DateTypeConfig parsed =
            (DateTypeConfig) serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml( "parseConfig.xml" ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void parseConfig()
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
            (DateTypeConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

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
            (DateTypeConfig) serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ).getDocumentElement() );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void serializeConfig()
    {
        final InputTypeConfig config = InputTypeConfig.create().
            property( "withTimezone", "true" ).
            build();

        final JsonNode json = this.serializer.serializeConfig( config );
        this.jsonHelper.assertJsonEquals( this.jsonHelper.loadTestJson( "serializeConfig.json" ), json );
    }
}
