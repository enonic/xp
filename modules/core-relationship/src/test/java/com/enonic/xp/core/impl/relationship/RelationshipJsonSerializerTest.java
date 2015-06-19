package com.enonic.xp.core.impl.relationship;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.support.SerializingTestHelper;

import static org.junit.Assert.*;

public class RelationshipJsonSerializerTest
{
    private static final Instant NOW = LocalDateTime.of( 2013, 1, 1, 12, 0 ).toInstant( ZoneOffset.UTC );

    private final SerializingTestHelper serializingTestHelper;

    public RelationshipJsonSerializerTest()
    {
        serializingTestHelper = new SerializingTestHelper( this, true );
    }

    @Before
    public void before()
    {

        //DateTimeUtils.setCurrentMillisFixed( NOW.toEpochMilli() );
    }

    private String jsonToString( final JsonNode node )
    {
        return serializingTestHelper.jsonToString( node );
    }

    @Test
    public void serialize_relationship()
    {
        Relationship relationship = Relationship.create().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( RelationshipTypeName.PARENT ).
            createdTime( NOW ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            build();

        RelationshipJsonSerializer serializer = new RelationshipJsonSerializer().
            includeCreator( true ).includeCreatedTime( true );

        // exercise
        JsonNode actualJson = serializer.serialize( relationship );

        ObjectNode expectedJson = JsonNodeFactory.instance.objectNode();
        expectedJson.put( "fromContent", "111" );
        expectedJson.put( "toContent", "222" );
        expectedJson.put( "type", RelationshipTypeName.PARENT.toString() );
        expectedJson.putNull( "managingData" );
        expectedJson.putNull( "properties" );
        expectedJson.put( "creator", "user:system:admin" );
        expectedJson.put( "createdTime", "2013-01-01T12:00:00Z" );

        assertEquals( "Serialization not as expected", expectedJson, actualJson );
    }

    @Test
    public void serialize_relationship_with_managingData()
    {
        Relationship relationship = Relationship.create().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( RelationshipTypeName.PARENT ).
            managed( PropertyPath.from( "mySet.myData" ) ).
            createdTime( NOW ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            build();

        RelationshipJsonSerializer serializer = new RelationshipJsonSerializer().
            includeCreator( true ).includeCreatedTime( true );

        // exercise
        JsonNode actualJson = serializer.serialize( relationship );

        ObjectNode expectedJson = JsonNodeFactory.instance.objectNode();
        expectedJson.put( "fromContent", "111" );
        expectedJson.put( "toContent", "222" );
        expectedJson.put( "type", RelationshipTypeName.PARENT.toString() );
        expectedJson.put( "managingData", "mySet.myData" );
        expectedJson.putNull( "properties" );
        expectedJson.put( "creator", "user:system:admin" );
        expectedJson.put( "createdTime", "2013-01-01T12:00:00Z" );

        assertEquals( "Serialization not as expected", expectedJson, actualJson );
    }

    @Test
    public void serialize_relationship_with_properties()
    {
        Relationship relationship = Relationship.create().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( RelationshipTypeName.PARENT ).
            property( "stars", "4" ).
            property( "stripes", "3" ).
            createdTime( NOW ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( NOW ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            build();

        RelationshipJsonSerializer serializer = new RelationshipJsonSerializer().
            includeCreator( true ).includeCreatedTime( true ).
            includeModifier( true ).includeModifiedTime( true );

        // exercise
        JsonNode actualJson = serializer.serialize( relationship );

        ObjectNode expectedJson = JsonNodeFactory.instance.objectNode();
        expectedJson.put( "fromContent", "111" );
        expectedJson.put( "toContent", "222" );
        expectedJson.put( "type", RelationshipTypeName.PARENT.toString() );
        expectedJson.putNull( "managingData" );
        ObjectNode propertiesNode = expectedJson.putObject( "properties" );
        propertiesNode.put( "stars", "4" );
        propertiesNode.put( "stripes", "3" );
        expectedJson.put( "creator", "user:system:admin" );
        expectedJson.put( "createdTime", "2013-01-01T12:00:00Z" );
        expectedJson.put( "modifier", "user:system:admin" );
        expectedJson.put( "modifiedTime", "2013-01-01T12:00:00Z" );

        assertEquals( "Serialization not as expected", expectedJson, actualJson );
    }

    @Test
    public void parse_relationship()
    {
        Relationship toSerialize = Relationship.create().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( RelationshipTypeName.PARENT ).
            managed( PropertyPath.from( "mySet.myData" ) ).
            property( "stars", "4" ).
            property( "stripes", "3" ).
            createdTime( NOW ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( NOW ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            build();

        RelationshipJsonSerializer serializer = new RelationshipJsonSerializer().
            includeCreator( true ).includeCreatedTime( true ).
            includeModifier( true ).includeModifiedTime( true );

        JsonNode json = serializer.serialize( toSerialize );

        // exercise
        Relationship parsedRelationship = serializer.toRelationship( jsonToString( json ) );

        assertEquals( ContentId.from( "111" ), parsedRelationship.getFromContent() );
        assertEquals( ContentId.from( "222" ), parsedRelationship.getToContent() );
        assertEquals( RelationshipTypeName.PARENT, parsedRelationship.getType() );
        assertEquals( true, parsedRelationship.isManaged() );
        assertEquals( PropertyPath.from( "mySet.myData" ), parsedRelationship.getManagingData() );
        assertEquals( "4", parsedRelationship.getProperty( "stars" ) );
        assertEquals( "3", parsedRelationship.getProperty( "stripes" ) );
        assertEquals( NOW, parsedRelationship.getCreatedTime() );
        assertEquals( PrincipalKey.from( "user:system:admin" ), parsedRelationship.getCreator() );
        assertEquals( NOW, parsedRelationship.getModifiedTime() );
        assertEquals( PrincipalKey.from( "user:system:admin" ), parsedRelationship.getModifier() );
    }
}
