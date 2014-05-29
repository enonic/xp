package com.enonic.wem.api.relationship;


import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

import static junit.framework.Assert.assertEquals;

public class RelationshipTest
{
    @Test
    public void build()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( ContentId.from( "a" ) );
        relationBuilder.toContent( ContentId.from( "b" ) );
        relationBuilder.createdTime( LocalDateTime.parse( "2012-01-01T12:00:00" ).toInstant( ZoneOffset.UTC ) );
        relationBuilder.creator( UserKey.from( "myStore:myUser" ) );
        relationBuilder.modifiedTime( LocalDateTime.parse( "2012-01-01T12:00:00" ).toInstant( ZoneOffset.UTC ) );
        relationBuilder.modifier( UserKey.from( "myStore:myUser" ) );
        relationBuilder.type( RelationshipTypeName.from( "like" ) );
        relationBuilder.property( "stars", "4" );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "a", relationship.getFromContent().toString() );
        assertEquals( "b", relationship.getToContent().toString() );
        assertEquals( "myUser", relationship.getCreator().getLocalName() );
        assertEquals( LocalDateTime.parse( "2012-01-01T12:00:00" ).toInstant( ZoneOffset.UTC ), relationship.getCreatedTime() );
        assertEquals( "myUser", relationship.getModifier().getLocalName() );
        assertEquals( LocalDateTime.parse( "2012-01-01T12:00:00" ).toInstant( ZoneOffset.UTC ), relationship.getModifiedTime() );
        assertEquals( "like", relationship.getType().toString() );
        assertEquals( "4", relationship.getProperty( "stars" ) );
    }

    @Test
    public void getKey_having_relationship_with_mangingData()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( ContentId.from( "a" ) );
        relationBuilder.toContent( ContentId.from( "b" ) );
        relationBuilder.type( RelationshipTypeName.from( "like" ) );
        relationBuilder.managed( DataPath.from( "myData" ) );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "like", relationship.getKey().getType().toString() );
        assertEquals( "a", relationship.getKey().getFromContent().toString() );
        assertEquals( "b", relationship.getKey().getToContent().toString() );
        assertEquals( "myData", relationship.getKey().getManagingData().toString() );
    }

    @Test
    public void getKey_having_relationship_without_mangingData()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( ContentId.from( "a" ) );
        relationBuilder.toContent( ContentId.from( "b" ) );
        relationBuilder.type( RelationshipTypeName.from( "like" ) );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "like", relationship.getKey().getType().toString() );
        assertEquals( "a", relationship.getKey().getFromContent().toString() );
        assertEquals( "b", relationship.getKey().getToContent().toString() );
        assertEquals( null, relationship.getKey().getManagingData() );
    }

    @Test(expected = NullPointerException.class)
    public void given_property_with_null_value_when_build_then_NullPointerExpception_is_thrown()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( ContentId.from( "a" ) );
        relationBuilder.toContent( ContentId.from( "b" ) );
        relationBuilder.type( RelationshipTypeName.from( "like" ) );
        relationBuilder.property( "key", null );

        // exercise
        relationBuilder.build();
    }

    @Test(expected = NullPointerException.class)
    public void given_property_with_null_key_when_build_then_NullPointerExpception_is_thrown()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( ContentId.from( "a" ) );
        relationBuilder.toContent( ContentId.from( "b" ) );
        relationBuilder.type( RelationshipTypeName.from( "like" ) );
        relationBuilder.property( null, "value" );

        // exercise
        relationBuilder.build();
    }

}
