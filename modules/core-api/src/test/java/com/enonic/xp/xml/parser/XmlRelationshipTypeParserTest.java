package com.enonic.xp.xml.parser;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.relationship.RelationshipType;

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
        this.parser.currentModule( ModuleKey.from( "mymodule" ) );

        this.builder = RelationshipType.newRelationshipType();
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
