package com.enonic.xp.schema.relationship;


import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void test_equals()
    {
        // setup
        RelationshipType.Builder builder = RelationshipType.newRelationshipType();
        builder.name( "mymodule:like" );
        builder.fromSemantic( "likes" );
        builder.toSemantic( "liked by" );
        builder.setAllowedFromTypes( ContentTypeNames.from( ContentTypeName.from( "mymodule:person" ) ) );
        builder.setAllowedToTypes( ContentTypeNames.from( ContentTypeName.from( "mymodule:person" ) ) );

        RelationshipType relationshipType1 = builder.build();
        builder = RelationshipType.newRelationshipType( relationshipType1 );
        RelationshipType relationshipType2 = builder.build();

        assertTrue( relationshipType1.equals( relationshipType1 ) );
        assertTrue( relationshipType1.equals( relationshipType2 ) );
        assertEquals( relationshipType1.hashCode(), relationshipType2.hashCode() );
        assertFalse( relationshipType1.equals( builder ) );

        RelationshipTypes relTypes = RelationshipTypes.from( RelationshipType.newRelationshipType().name( "mymodule:like" ).build() );
        assertEquals( relTypes, RelationshipTypes.from( relTypes ) );
    }

    @Test
    public void test_immutable_relationship_types()
    {
        RelationshipTypes relTypes = RelationshipTypes.empty();
        assertTrue( relTypes.getSize() == 0 );
        try{
            relTypes.getList().add( null );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e instanceof UnsupportedOperationException );
        }
        relTypes = RelationshipTypes.from( RelationshipType.newRelationshipType().name( "mymodule:like" ).build() );
        try {
            relTypes.getList().add( null );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e instanceof UnsupportedOperationException );
        }
        try {
            relTypes.getNames().add( null );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e instanceof UnsupportedOperationException );
        }
    }
}
