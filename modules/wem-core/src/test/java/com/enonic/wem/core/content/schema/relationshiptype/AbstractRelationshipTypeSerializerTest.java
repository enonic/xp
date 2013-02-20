package com.enonic.wem.core.content.schema.relationshiptype;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.AbstractSerializerTest;

import static org.junit.Assert.*;


public abstract class AbstractRelationshipTypeSerializerTest
    extends AbstractSerializerTest
{
    private RelationshipTypeSerializer serializer;

    abstract RelationshipTypeSerializer getSerializer();

    abstract String getFileAsString( String fileName );


    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    abstract void assertSerializedResult( String fileNameForExpected, String actualSerialization );

    @Test
    public void generate_withAllowedTypes()
        throws Exception
    {
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.name( "love" );
        builder.displayName( "Love" );
        builder.module( ModuleName.SYSTEM );
        builder.fromSemantic( "loves" );
        builder.toSemantic( "loved by" );
        builder.addAllowedFromType( new QualifiedContentTypeName( ModuleName.SYSTEM, "person" ) );
        builder.addAllowedFromType( new QualifiedContentTypeName( ModuleName.SYSTEM, "animal" ) );
        builder.addAllowedToType( new QualifiedContentTypeName( ModuleName.SYSTEM, "person" ) );
        RelationshipType relationshipType = builder.build();

        // exercise
        String serialized = toString( relationshipType );

        assertSerializedResult( "relationshipType_withAllowedTypes", serialized );
    }

    @Test
    public void generate_withoutAllowedTypes()
        throws Exception
    {
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.name( "love" );
        builder.displayName( "Love" );
        builder.module( ModuleName.SYSTEM );
        builder.fromSemantic( "loves" );
        builder.toSemantic( "loved by" );
        RelationshipType relationshipType = builder.build();

        // exercise
        String serialized = toString( relationshipType );

        assertSerializedResult( "relationshipType_withoutAllowedTypes", serialized );
    }

    @Test
    public void parse_withAllowedTypes()
        throws Exception
    {
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.name( "love" );
        builder.displayName( "Love" );
        builder.module( ModuleName.SYSTEM );
        builder.fromSemantic( "loves" );
        builder.toSemantic( "loved by" );
        builder.addAllowedFromType( new QualifiedContentTypeName( ModuleName.SYSTEM, "person" ) );
        builder.addAllowedFromType( new QualifiedContentTypeName( ModuleName.SYSTEM, "animal" ) );
        builder.addAllowedToType( new QualifiedContentTypeName( ModuleName.SYSTEM, "person" ) );
        RelationshipType relationshipType = builder.build();

        // exercise
        RelationshipType parsed = toRelationshipType( getFileAsString( "relationshipType_withAllowedTypes" ) );
        //assertSerializedResult( "relationshipType", serialized );
    }

    @Test
    public void parse_withoutAllowedTypes()
        throws Exception
    {
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.name( "love" );
        builder.displayName( "Love" );
        builder.module( ModuleName.SYSTEM );
        builder.fromSemantic( "loves" );
        builder.toSemantic( "loved by" );
        RelationshipType relationshipType = builder.build();

        // exercise
        RelationshipType parsed = toRelationshipType( getFileAsString( "relationshipType_withAllowedTypes" ) );
        //assertSerializedResult( "relationshipType", serialized );
    }

    @Test
    public void serialize_roundTrip_withAllowedTypes()
        throws Exception
    {
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.name( "love" );
        builder.displayName( "Love" );
        builder.module( ModuleName.SYSTEM );
        builder.fromSemantic( "loves" );
        builder.toSemantic( "loved by" );
        builder.addAllowedFromType( new QualifiedContentTypeName( ModuleName.SYSTEM, "person" ) );
        builder.addAllowedFromType( new QualifiedContentTypeName( ModuleName.SYSTEM, "animal" ) );
        builder.addAllowedToType( new QualifiedContentTypeName( ModuleName.SYSTEM, "person" ) );
        RelationshipType relationshipType = builder.build();

        // exercise
        String serialized = toString( relationshipType );

        // exercise
        RelationshipType parsedRelationshipType = toRelationshipType( serialized );
        String serializedAfterParsing = toString( parsedRelationshipType );

        // verify
        assertEquals( serialized, serializedAfterParsing );
    }


    @Test
    public void serialize_roundTrip_withoutAllowedTypes()
        throws Exception
    {
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.name( "love" );
        builder.displayName( "Love" );
        builder.module( ModuleName.SYSTEM );
        builder.fromSemantic( "loves" );
        builder.toSemantic( "loved by" );
        RelationshipType relationshipType = builder.build();

        // exercise
        String serialized = toString( relationshipType );

        // exercise
        RelationshipType parsedRelationshipType = toRelationshipType( serialized );
        String serializedAfterParsing = toString( parsedRelationshipType );

        // verify
        assertEquals( serialized, serializedAfterParsing );
    }

    private RelationshipType toRelationshipType( String serialized )
    {
        return serializer.toRelationshipType( serialized );
    }

    private String toString( RelationshipType type )
    {
        String serialized = getSerializer().toString( type );
        System.out.println( "RelationshipType:" );
        System.out.println( serialized );
        return serialized;
    }


}
