package com.enonic.xp.core.form.inputtype;


import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.enonic.xp.core.form.inputtype.ContentSelectorConfig;
import com.enonic.xp.core.form.inputtype.ContentSelectorConfigXmlSerializer;
import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.schema.content.ContentTypeName;
import com.enonic.xp.core.schema.relationship.RelationshipTypeName;
import com.enonic.xp.core.support.XmlTestHelper;
import com.enonic.xp.core.xml.DomHelper;

import static junit.framework.Assert.assertEquals;

public class ContentSelectorConfigXmlSerializerTest
{
    private final static ModuleKey CURRENT_MODULE = ModuleKey.from( "mymodule" );

    private XmlTestHelper xmlHelper;

    private ContentSelectorConfigXmlSerializer serializer = new ContentSelectorConfigXmlSerializer();

    @Before
    public void before()
    {
        xmlHelper = new XmlTestHelper( this );
    }

    @Test
    public void serializeConfig()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig config = builder.build();

        // exercise
        final Document doc = serializer.generate( config );

        // verify
        assertEquals( xmlHelper.loadTestXml2( "serializeConfig.xml" ), DomHelper.serialize( doc ) );
    }

    @Test
    public void serializeConfig_with_allowed_content_types()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        builder.addAllowedContentType( ContentTypeName.imageMedia() );
        builder.addAllowedContentType( ContentTypeName.videoMedia() );
        ContentSelectorConfig config = builder.build();

        // exercise
        final Document doc = serializer.generate( config );

        // verify
        assertEquals( xmlHelper.loadTestXml2( "serializeFullConfig.xml" ), DomHelper.serialize( doc ) );
    }

    @Test
    public void serializeConfig_empty_with_default_values()
        throws IOException
    {
        // setup
        ContentSelectorConfig config = ContentSelectorConfig.newRelationshipConfig().build();

        // exercise
        final Document doc = serializer.generate( config );

        // verify
        assertEquals( xmlHelper.loadTestXml2( "serializeEmptyConfig.xml" ), DomHelper.serialize( doc ) );
    }

    @Test
    public void parseConfig()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig expected = builder.build();

        // exercise
        ContentSelectorConfig parsed = serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml( "parseConfig.xml" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_with_allowed_content_types()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        builder.addAllowedContentType( ContentTypeName.imageMedia() );
        builder.addAllowedContentType( ContentTypeName.videoMedia() );
        ContentSelectorConfig expected = builder.build();

        // exercise
        ContentSelectorConfig parsed = serializer.parseConfig( CURRENT_MODULE, xmlHelper.parseXml( "parseFullConfig.xml" ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_with_contentTypeFilter_as_empty()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        builder.relationshipType( RelationshipTypeName.REFERENCE );
        ContentSelectorConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<content-type-filter></content-type-filter>" );
        xml.append( "<relationship-type>system:reference</relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ContentSelectorConfig parsed = serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_as_empty()
        throws IOException
    {
        // setup
        ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        ContentSelectorConfig expected = builder.build();

        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "<relationship-type></relationship-type>" );
        xml.append( "</config>\n" );

        // exercise
        ContentSelectorConfig parsed = serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }

    @Test
    public void parseConfig_relationshipType_not_existing()
        throws IOException
    {
        // setup
        StringBuilder xml = new StringBuilder();
        xml.append( "<config>\n" );
        xml.append( "</config>\n" );
        ContentSelectorConfig expected = ContentSelectorConfig.newRelationshipConfig().build();


        // exercise
        ContentSelectorConfig parsed = serializer.parseConfig( CURRENT_MODULE, DomHelper.parse( xml.toString() ) );

        // verify
        assertEquals( expected.getRelationshipType(), parsed.getRelationshipType() );
    }
}
