package com.enonic.xp.relationship;


import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

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
        relationBuilder.creator( PrincipalKey.from( "user:myStore:myUser" ) );
        relationBuilder.modifiedTime( LocalDateTime.parse( "2012-01-01T12:00:00" ).toInstant( ZoneOffset.UTC ) );
        relationBuilder.modifier( PrincipalKey.from( "user:myStore:myUser" ) );
        relationBuilder.type( RelationshipTypeName.from( "system:reference" ) );
        relationBuilder.property( "stars", "4" );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "a", relationship.getFromContent().toString() );
        assertEquals( "b", relationship.getToContent().toString() );
        assertEquals( "myUser", relationship.getCreator().getId() );
        assertEquals( LocalDateTime.parse( "2012-01-01T12:00:00" ).toInstant( ZoneOffset.UTC ), relationship.getCreatedTime() );
        assertEquals( "myUser", relationship.getModifier().getId() );
        assertEquals( LocalDateTime.parse( "2012-01-01T12:00:00" ).toInstant( ZoneOffset.UTC ), relationship.getModifiedTime() );
        assertEquals( "system:reference", relationship.getType().toString() );
        assertEquals( "4", relationship.getProperty( "stars" ) );
    }

    @Test
    public void getKey_having_relationship_with_mangingData()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( ContentId.from( "a" ) );
        relationBuilder.toContent( ContentId.from( "b" ) );
        relationBuilder.type( RelationshipTypeName.from( "system:reference" ) );
        relationBuilder.managed( PropertyPath.from( "myData" ) );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "system:reference", relationship.getKey().getType().toString() );
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
        relationBuilder.type( RelationshipTypeName.from( "system:reference" ) );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "system:reference", relationship.getKey().getType().toString() );
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
        relationBuilder.type( RelationshipTypeName.from( "system:reference" ) );
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
        relationBuilder.type( RelationshipTypeName.from( "system:reference" ) );
        relationBuilder.property( null, "value" );

        // exercise
        relationBuilder.build();
    }

}
