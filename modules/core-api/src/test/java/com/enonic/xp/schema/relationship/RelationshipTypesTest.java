package com.enonic.xp.schema.relationship;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class RelationshipTypesTest
{

    @Test
    public void add_array()
    {
        RelationshipTypes relationshipTypes = RelationshipTypes.empty();

        RelationshipType[] relationshipTypesArray =
            {RelationshipType.create().name( "mymodule:like" ).build(), RelationshipType.create().name( "mymodule:person" ).build()};

        RelationshipTypes newRelationshipTypes = relationshipTypes.add( relationshipTypesArray );

        assertEquals( 0, relationshipTypes.getSize() );
        assertEquals( 2, newRelationshipTypes.getSize() );
    }

    @Test
    public void add_iterable()
    {
        RelationshipTypes relationshipTypes = RelationshipTypes.empty();

        List<RelationshipType> relationshipTypesList = Lists.newArrayList( RelationshipType.create().name( "mymodule:like" ).build(),
                                                                           RelationshipType.create().name( "mymodule:person" ).build() );

        RelationshipTypes newRelationshipTypes = relationshipTypes.add( relationshipTypesList );

        assertEquals( 0, relationshipTypes.getSize() );
        assertEquals( 2, newRelationshipTypes.getSize() );
    }

    @Test
    public void test_equals()
    {
        RelationshipTypes relTypes = RelationshipTypes.from( RelationshipType.create().name( "mymodule:like" ).build() );
        assertEquals( relTypes, RelationshipTypes.from( relTypes ) );
    }

    @Test
    public void test_immutable_relationship_types()
    {
        RelationshipTypes relTypes = RelationshipTypes.empty();
        assertTrue( relTypes.getSize() == 0 );
        try
        {
            relTypes.getList().add( null );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e instanceof UnsupportedOperationException );
        }
        relTypes = RelationshipTypes.from( RelationshipType.create().name( "mymodule:like" ).build() );
        try
        {
            relTypes.getList().add( null );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e instanceof UnsupportedOperationException );
        }
        try
        {
            relTypes.getNames().add( null );
        }
        catch ( Exception e )
        {
            Assert.assertTrue( e instanceof UnsupportedOperationException );
        }
    }

    @Test
    public void from()
    {
        RelationshipType[] relationshipTypesArray =
            {RelationshipType.create().name( "mymodule:like" ).build(), RelationshipType.create().name( "mymodule:person" ).build(),
                RelationshipType.create().name( "mymodule:site" ).build()};

        RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipTypesArray );

        List<RelationshipType> relationshipTypesList = Lists.newArrayList( RelationshipType.create().name( "mymodule:like" ).build(),
                                                                           RelationshipType.create().name( "mymodule:person" ).build() );

        assertEquals( 3, relationshipTypes.getSize() );
        assertEquals( 3, RelationshipTypes.from( relationshipTypes ).getSize() );
        assertEquals( 2, RelationshipTypes.from( relationshipTypesList ).getSize() );
    }
}
