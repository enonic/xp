package com.enonic.xp.schema.relationship;


import org.junit.Test;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.relationship.RelationshipType;

import static junit.framework.Assert.assertEquals;

public class RelationshipTypeTest
{
    @Test
    public void build()
    {
        // setup
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.name( "mymodule:like" );
        builder.fromSemantic( "likes" );
        builder.toSemantic( "liked by" );
        builder.addAllowedFromType( ContentTypeName.from( "mymodule:person" ) );
        builder.addAllowedToType( ContentTypeName.from( "mymodule:person" ) );

        // exercise
        RelationshipType relationshipType = builder.build();

        // verify
        assertEquals( "mymodule:like", relationshipType.getName().toString() );
        assertEquals( "likes", relationshipType.getFromSemantic() );
        assertEquals( "liked by", relationshipType.getToSemantic() );
        assertEquals( ContentTypeNames.from( "mymodule:person" ), relationshipType.getAllowedFromTypes() );
        assertEquals( ContentTypeNames.from( "mymodule:person" ), relationshipType.getAllowedToTypes() );
    }
}
