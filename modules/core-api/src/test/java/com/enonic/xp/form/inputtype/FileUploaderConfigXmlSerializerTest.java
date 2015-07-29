package com.enonic.xp.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.XmlTestHelper;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class FileUploaderConfigXmlSerializerTest
{
    private final static ApplicationKey CURRENT_APP = ApplicationKey.from( "myapp" );

    private XmlTestHelper xmlHelper;

    private FileUploaderConfigXmlSerializer serializer = new FileUploaderConfigXmlSerializer();

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
        FileUploaderConfig config = FileUploaderConfig.create().
            allowType( "Images", "jpg,png,gif" ).
            allowType( "Text", "txt,doc" ).
            hideDropZone( true ).
            build();

        // exercise
        final Document doc = serializer.generate( config );

        // verify
        assertEquals( xmlHelper.loadTestXml( "serializeConfig.xml" ), DomHelper.serialize( doc ) );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        FileUploaderConfig expected = FileUploaderConfig.create().
            allowType( "Images", "jpg,png,gif" ).
            allowType( "Text", "txt,doc" ).
            hideDropZone( true ).
            build();

        // exercise
        FileUploaderConfig parsed = serializer.parseConfig( CURRENT_APP, xmlHelper.parseXml( "parseConfig.xml" ) );

        // verify
        assertEquals( expected, parsed );
    }

    @Test
    public void parseConfig_with_allowTypes_as_empty()
        throws IOException
    {
        // setup
        FileUploaderConfig expected = FileUploaderConfig.create().build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<allow-types></allow-types>" );
        xml.append( "</config>\n" );

        // exercise
        FileUploaderConfig parsed = serializer.parseConfig( CURRENT_APP, DomHelper.parse( xml.toString() ) );

        // verify
        assertEquals( expected, parsed );
    }

    @Test
    public void parseConfig_allowTypes_not_specified()
        throws IOException
    {
        // setup
        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "</config>\n" );
        FileUploaderConfig expected = FileUploaderConfig.create().build();

        // exercise
        FileUploaderConfig parsed = serializer.parseConfig( CURRENT_APP, DomHelper.parse( xml.toString() ) );

        // verify
        assertEquals( expected, parsed );
    }
}
