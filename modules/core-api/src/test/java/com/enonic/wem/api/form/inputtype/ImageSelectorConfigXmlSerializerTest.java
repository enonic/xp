package com.enonic.wem.api.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.support.XmlTestHelper;
import com.enonic.wem.api.xml.DomHelper;

import static com.enonic.wem.api.form.inputtype.ImageSelectorConfig.newImageSelectorConfig;
import static junit.framework.Assert.assertEquals;

public class ImageSelectorConfigXmlSerializerTest
{
    private XmlTestHelper xmlHelper;

    private ImageSelectorConfigXmlSerializer serializer = new ImageSelectorConfigXmlSerializer();

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
        ImageSelectorConfig.Builder builder = newImageSelectorConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ImageSelectorConfig config = builder.build();

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
        ImageSelectorConfig.Builder builder = newImageSelectorConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ImageSelectorConfig expected = builder.build();

        // exercise
        ImageSelectorConfig parsed = serializer.parseConfig( xmlHelper.parseXml( "parseConfig.xml" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }


    @Test
    public void parseConfig_with_contentTypeFilter_as_empty()
        throws IOException
    {
        // setup
        ImageSelectorConfig.Builder builder = newImageSelectorConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ImageSelectorConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter></content-type-filter>" );
        xml.append( "<relationship-type>system:reference</relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ImageSelectorConfig parsed = serializer.parseConfig( DomHelper.parse( xml.toString() ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test(expected = NullPointerException.class)
    public void parseConfig_relationshipType_as_empty()
        throws IOException
    {
        // setup
        ImageSelectorConfig.Builder builder = ImageSelectorConfig.newImageSelectorConfig();
        ImageSelectorConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<relationship-type></relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ImageSelectorConfig parsed = serializer.parseConfig( DomHelper.parse( xml.toString() ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test(expected = NullPointerException.class)
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "</config>\n" );

        // exercise
        serializer.parseConfig( DomHelper.parse( xml.toString() ) );
    }
}
