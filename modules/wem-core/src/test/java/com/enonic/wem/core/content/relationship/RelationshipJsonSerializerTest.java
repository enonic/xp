package com.enonic.wem.core.content.relationship;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.content.relationship.Relationship;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.core.AbstractSerializerTest;

import static org.junit.Assert.*;

public class RelationshipJsonSerializerTest
    extends AbstractSerializerTest
{
    private static final DateTime NOW = new DateTime( 2013, 1, 1, 12, 0, DateTimeZone.UTC );

    @Before
    public void before()
    {
        DateTimeUtils.setCurrentMillisFixed( NOW.getMillis() );
    }

    @Test
    public void serialize_relationship()
    {
        Relationship relationship = Relationship.newRelationship().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( QualifiedRelationshipTypeName.PARENT ).
            createdTime( NOW ).
            creator( AccountKey.superUser() ).
            build();

        RelationshipJsonSerializer serializer = new RelationshipJsonSerializer().
            includeCreator( true ).includeCreatedTime( true );

        // exercise
        JsonNode actualJson = serializer.serialize( relationship );

        ObjectNode expectedJson = JsonNodeFactory.instance.objectNode();
        expectedJson.put( "fromContent", "111" );
        expectedJson.put( "toContent", "222" );
        expectedJson.put( "type", QualifiedRelationshipTypeName.PARENT.toString() );
        expectedJson.putNull( "managingData" );
        expectedJson.putNull( "properties" );
        expectedJson.put( "creator", "user:system:admin" );
        expectedJson.put( "createdTime", "2013-01-01T12:00:00.000Z" );

        assertEquals( "Serialization not as expected", expectedJson, actualJson );
    }

    @Test
    public void serialize_relationship_with_managingData()
    {
        Relationship relationship = Relationship.newRelationship().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( QualifiedRelationshipTypeName.PARENT ).
            managed( DataPath.from( "mySet.myData" ) ).
            createdTime( NOW ).
            creator( AccountKey.superUser() ).
            build();

        RelationshipJsonSerializer serializer = new RelationshipJsonSerializer().
            includeCreator( true ).includeCreatedTime( true );

        // exercise
        JsonNode actualJson = serializer.serialize( relationship );

        ObjectNode expectedJson = JsonNodeFactory.instance.objectNode();
        expectedJson.put( "fromContent", "111" );
        expectedJson.put( "toContent", "222" );
        expectedJson.put( "type", QualifiedRelationshipTypeName.PARENT.toString() );
        expectedJson.put( "managingData", "mySet.myData" );
        expectedJson.putNull( "properties" );
        expectedJson.put( "creator", "user:system:admin" );
        expectedJson.put( "createdTime", "2013-01-01T12:00:00.000Z" );

        assertEquals( "Serialization not as expected", expectedJson, actualJson );
    }

    @Test
    public void serialize_relationship_with_properties()
    {
        Relationship relationship = Relationship.newRelationship().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( QualifiedRelationshipTypeName.PARENT ).
            property( "stars", "4" ).
            property( "stripes", "3" ).
            createdTime( NOW ).
            creator( AccountKey.superUser() ).
            modifiedTime( NOW ).
            modifier( AccountKey.superUser() ).
            build();

        RelationshipJsonSerializer serializer = new RelationshipJsonSerializer().
            includeCreator( true ).includeCreatedTime( true ).
            includeModifier( true ).includeModifiedTime( true );

        // exercise
        JsonNode actualJson = serializer.serialize( relationship );

        ObjectNode expectedJson = JsonNodeFactory.instance.objectNode();
        expectedJson.put( "fromContent", "111" );
        expectedJson.put( "toContent", "222" );
        expectedJson.put( "type", QualifiedRelationshipTypeName.PARENT.toString() );
        expectedJson.putNull( "managingData" );
        ObjectNode propertiesNode = expectedJson.putObject( "properties" );
        propertiesNode.put( "stars", "4" );
        propertiesNode.put( "stripes", "3" );
        expectedJson.put( "creator", "user:system:admin" );
        expectedJson.put( "createdTime", "2013-01-01T12:00:00.000Z" );
        expectedJson.put( "modifier", "user:system:admin" );
        expectedJson.put( "modifiedTime", "2013-01-01T12:00:00.000Z" );

        assertEquals( "Serialization not as expected", expectedJson, actualJson );
    }

    @Test
    public void parse_relationship()
    {
        Relationship toSerialize = Relationship.newRelationship().
            fromContent( ContentId.from( "111" ) ).
            toContent( ContentId.from( "222" ) ).
            type( QualifiedRelationshipTypeName.PARENT ).
            managed( DataPath.from( "mySet.myData" ) ).
            property( "stars", "4" ).
            property( "stripes", "3" ).
            createdTime( NOW ).
            creator( AccountKey.superUser() ).
            modifiedTime( NOW ).
            modifier( AccountKey.superUser() ).
            build();

        RelationshipJsonSerializer serializer = new RelationshipJsonSerializer().
            includeCreator( true ).includeCreatedTime( true ).
            includeModifier( true ).includeModifiedTime( true );

        JsonNode json = serializer.serialize( toSerialize );

        // exercise
        Relationship parsedRelationship = serializer.toRelationship( jsonToString( json ) );

        assertEquals( ContentId.from( "111" ), parsedRelationship.getFromContent() );
        assertEquals( ContentId.from( "222" ), parsedRelationship.getToContent() );
        assertEquals( QualifiedRelationshipTypeName.PARENT, parsedRelationship.getType() );
        assertEquals( true, parsedRelationship.isManaged() );
        assertEquals( DataPath.from( "mySet.myData" ), parsedRelationship.getManagingData() );
        assertEquals( "4", parsedRelationship.getProperty( "stars" ) );
        assertEquals( "3", parsedRelationship.getProperty( "stripes" ) );
        assertEquals( NOW, parsedRelationship.getCreatedTime() );
        assertEquals( AccountKey.superUser(), parsedRelationship.getCreator() );
        assertEquals( NOW, parsedRelationship.getModifiedTime() );
        assertEquals( AccountKey.superUser(), parsedRelationship.getModifier() );
    }
}
