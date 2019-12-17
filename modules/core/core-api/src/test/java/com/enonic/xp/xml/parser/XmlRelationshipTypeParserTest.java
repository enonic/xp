package com.enonic.xp.xml.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.relationship.RelationshipType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlRelationshipTypeParserTest
    extends XmlModelParserTest
{
    private XmlRelationshipTypeParser parser;

    private RelationshipType.Builder builder;

    @BeforeEach
    public void setup()
    {
        this.parser = new XmlRelationshipTypeParser();
        this.parser.currentApplication( ApplicationKey.from( "myapplication" ) );

        this.builder = RelationshipType.create();
        this.builder.name( "myapplication:name" );
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
        assertEquals( "myapplication:name", result.getName().toString() );
        assertEquals( "description", result.getDescription() );
        assertEquals( "likes", result.getFromSemantic() );
        assertEquals( "liked by", result.getToSemantic() );
        assertEquals( "[myapplication:animal, myapplication:person]", result.getAllowedFromTypes().toString() );
        assertEquals( "[myapplication:vehicle]", result.getAllowedToTypes().toString() );
    }
}
