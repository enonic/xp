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
        return this.serializer.parseConfig( CURRENT_APPLICATION, this.xmlHelper.parseXml( name ).getDocumentElement() );
    }

    @Test
    public void parseConfig()
    {
        final InputTypeConfig parsed = parse( "parseConfig.xml" );
        assertEquals( true, parsed.getValue( "withTimezone", boolean.class ) );
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
