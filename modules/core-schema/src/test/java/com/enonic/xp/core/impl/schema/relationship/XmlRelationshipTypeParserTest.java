package com.enonic.xp.core.impl.schema.relationship;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.xml.parser.XmlModelParserTest;

import static org.junit.Assert.*;

public class XmlRelationshipTypeParserTest
    extends XmlModelParserTest
{
    private XmlRelationshipTypeParser parser;

    private RelationshipType.Builder builder;

    @Before
    public void setup()
    {
        this.parser = new XmlRelationshipTypeParser();
        this.parser.currentApplication( ApplicationKey.from( "mymodule" ) );

        this.builder = RelationshipType.create();
        this.builder.name( "mymodule:name" );
        this.parser.builder( this.builder );
    }

    @Test
    public void testParse()
        throws Exception
    {
        parse( this.parser, ".xml" );
        assertResult();
    }

    @Test
    public void testParse_noNs()
        throws Exception
    {
        parseRemoveNs( this.parser, ".xml" );
        assertResult();
    }

    private void assertResult()
        throws Exception
    {
        final RelationshipType result = this.builder.build();
        assertEquals( "mymodule:name", result.getName().toString() );
        assertEquals( "description", result.getDescription() );
        assertEquals( "likes", result.getFromSemantic() );
        assertEquals( "liked by", result.getToSemantic() );
        assertEquals( "[mymodule:animal, mymodule:person]", result.getAllowedFromTypes().toString() );
        assertEquals( "[mymodule:vehicle]", result.getAllowedToTypes().toString() );
    }
}
