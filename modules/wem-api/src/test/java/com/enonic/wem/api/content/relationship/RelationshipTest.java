package com.enonic.wem.api.content.relationship;


import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.MockContentId;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;

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
        relationBuilder.creator( UserKey.user( "myStore:myUser" ) );
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
}
