package com.enonic.wem.api.content.relation;


import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;

import static junit.framework.Assert.assertEquals;

public class RelationshipTest
{
    @Test
    public void build()
    {
        // setup
        RelationshipType.Builder typeBuilder = RelationshipType.newRelationType();
        typeBuilder.module( ModuleName.from( "myModule" ) );
        typeBuilder.name( "like" );
        typeBuilder.fromSemantic( "likes" );
        typeBuilder.toSemantic( "liked by" );
        typeBuilder.addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) );
        typeBuilder.addAllowedToType( new QualifiedContentTypeName( "myModule:person" ) );
        RelationshipType type = typeBuilder.build();

        final Relationship.Builder relationBuilder = Relationship.newRelation();
        relationBuilder.from( ContentPath.from( "a" ) );
        relationBuilder.to( ContentPath.from( "b" ) );
        relationBuilder.createdTime( DateTime.parse( "2012-01-01T12:00:00" ) );
        relationBuilder.creator( UserKey.user( "myStore:myUser" ) );
        relationBuilder.type( type );
        //relationBuilder.addProperty( "stars", "4" );

        // exercise
        Relationship relationship = relationBuilder.build();

        // verify
        assertEquals( "a", relationship.getFromContent().toString() );
        assertEquals( "b", relationship.getToContent().toString() );
        assertEquals( "myUser", relationship.getCreator().getLocalName() );
        assertEquals( DateTime.parse( "2012-01-01T12:00:00" ), relationship.getCreatedTime() );
        assertEquals( "like", relationship.getType().getName() );
    }
}
