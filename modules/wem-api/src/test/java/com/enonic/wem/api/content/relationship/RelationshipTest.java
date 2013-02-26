package com.enonic.wem.api.content.relationship;


import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.MockContentId;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static junit.framework.Assert.assertEquals;

public class RelationshipTest
{
    @Test
    public void build()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( MockContentId.from( "a" ) );
        relationBuilder.toContent( MockContentId.from( "b" ) );
        relationBuilder.createdTime( DateTime.parse( "2012-01-01T12:00:00" ) );
        relationBuilder.creator( UserKey.from( "myStore:myUser" ) );
        relationBuilder.type( QualifiedRelationshipTypeName.from( "system:like" ) );
        relationBuilder.property( "stars", "4" );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "a", relationship.getFromContent().toString() );
        assertEquals( "b", relationship.getToContent().toString() );
        assertEquals( "myUser", relationship.getCreator().getLocalName() );
        assertEquals( DateTime.parse( "2012-01-01T12:00:00" ), relationship.getCreatedTime() );
        assertEquals( "like", relationship.getType().getLocalName() );
        assertEquals( "4", relationship.getProperty( "stars" ) );
    }

    @Test
    public void getKey_having_relationship_with_mangingData()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( MockContentId.from( "a" ) );
        relationBuilder.toContent( MockContentId.from( "b" ) );
        relationBuilder.type( QualifiedRelationshipTypeName.from( "system:like" ) );
        relationBuilder.managed( EntryPath.from( "myData" ) );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "system:like", relationship.getKey().getType().toString() );
        assertEquals( "a", relationship.getKey().getFromContent().toString() );
        assertEquals( "b", relationship.getKey().getToContent().toString() );
        assertEquals( "myData", relationship.getKey().getManagingData().toString() );
    }

    @Test
    public void getKey_having_relationship_without_mangingData()
    {
        // setup
        final Relationship.Builder relationBuilder = Relationship.newRelationship();
        relationBuilder.fromContent( MockContentId.from( "a" ) );
        relationBuilder.toContent( MockContentId.from( "b" ) );
        relationBuilder.type( QualifiedRelationshipTypeName.from( "system:like" ) );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "system:like", relationship.getKey().getType().toString() );
        assertEquals( "a", relationship.getKey().getFromContent().toString() );
        assertEquals( "b", relationship.getKey().getToContent().toString() );
        assertEquals( null, relationship.getKey().getManagingData() );
    }

}
