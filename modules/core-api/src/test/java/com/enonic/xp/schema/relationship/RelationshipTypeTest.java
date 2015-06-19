package com.enonic.xp.schema.relationship;


import org.junit.Test;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

import static org.junit.Assert.*;

public class RelationshipTypeTest
{
    @Test
    public void build()
    {
        // setup
        RelationshipType.Builder builder = RelationshipType.create();
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

    @Test
    public void test_equals()
    {
        // setup
        RelationshipType.Builder builder = RelationshipType.create();
        builder.name( "mymodule:like" );
        builder.fromSemantic( "likes" );
        builder.toSemantic( "liked by" );
        builder.setAllowedFromTypes( ContentTypeNames.from( ContentTypeName.from( "mymodule:person" ) ) );
        builder.setAllowedToTypes( ContentTypeNames.from( ContentTypeName.from( "mymodule:person" ) ) );

        RelationshipType relationshipType1 = builder.build();
        builder = RelationshipType.create( relationshipType1 );
        RelationshipType relationshipType2 = builder.build();

        assertTrue( relationshipType1.equals( relationshipType1 ) );
        assertTrue( relationshipType1.equals( relationshipType2 ) );
        assertEquals( relationshipType1.hashCode(), relationshipType2.hashCode() );
        assertFalse( relationshipType1.equals( builder ) );
    }

}
