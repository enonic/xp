package com.enonic.xp.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.support.XmlTestHelper;
import com.enonic.xp.xml.DomHelper;

import static com.enonic.xp.form.inputtype.DateConfig.newDateConfig;
import static org.junit.Assert.*;

public class DateConfigXmlSerializerTest
{
    private final static ModuleKey CURRENT_MODULE = ModuleKey.from( "mymodule" );

    private XmlTestHelper xmlHelper;

    private DateConfigXmlSerializer serializer = new DateConfigXmlSerializer();

    @Before
    public void before()
    {
        xmlHelper = new XmlTestHelper( this );
    }

    @Test
    public void serializeConfig()
        throws IOException, SAXException
    {
        // setup
        DateConfig.Builder builder = newDateConfig();
        builder.withTimezone( true );
        DateConfig config = builder.build();

        // exercise
        final Document doc = serializer.generate( config );

        // verify
        assertEquals( xmlHelper.loadTestXml2( "serializeConfig.xml" ), DomHelper.serialize( doc ) );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        DateConfig.Builder builder = newDateConfig();
        builder.withTimezone( true );
        DateConfig expected = builder.build();

        // exercise
        DateConfig parsed = serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml( "parseConfig.xml" ) );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }

    @Test
    public void parseConfig_timezone_as_empty()
        throws IOException
    {
        // setup
        DateConfig.Builder builder = DateConfig.newDateConfig();
        DateConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<with-timezone></with-timezone>" );
        xml.append( "</config>\n" );

        // exercise
        DateConfig parsed = serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ) );

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
        DateConfig expected = DateConfig.newDateConfig().build();

        // exercise
        DateConfig parsed = serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ) );

        // verify
        assertEquals( expected.isWithTimezone(), parsed.isWithTimezone() );
    }
}
