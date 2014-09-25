package com.enonic.wem.core.schema.relationship.dao;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.SchemaRegistry;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

import static org.junit.Assert.*;

public class RelationshipTypeDaoImplTest
{
    private SchemaRegistry schemaRegistry;

    private RelationshipTypeDaoImpl relationshipTypeDao;

    @Before
    public void setupDao()
        throws Exception
    {
        this.schemaRegistry = Mockito.mock( SchemaRegistry.class );
        this.relationshipTypeDao = new RelationshipTypeDaoImpl();
        this.relationshipTypeDao.setSchemaRegistry( this.schemaRegistry );
    }

    @Test
    public void selectRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "mymodule:like" ).
            displayName( "Like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule:person" ) ).
            build();

        Mockito.when( this.schemaRegistry.getRelationshipType( RelationshipTypeName.from( "system-0.0.0:like" ) ) ).thenReturn( like );

        // exercise
        RelationshipType createdRelationshipType =
            relationshipTypeDao.getRelationshipType( RelationshipTypeName.from( "system-0.0.0:like" ) );

        // verify
        assertNotNull( createdRelationshipType );
        assertEquals( "mymodule:like", createdRelationshipType.getName().toString() );
        assertEquals( like, createdRelationshipType );
    }

    @Test
    public void selectAllRelationshipTypes()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "mymodule:like" ).
            displayName( "Like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule:thing" ) ).
            build();

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "mymodule:hate" ).
            displayName( "Hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule:thing" ) ).
            build();

        Mockito.when( this.schemaRegistry.getAllRelationshipTypes() ).thenReturn( RelationshipTypes.from( like, hates ) );

        // exercise
        RelationshipTypes relationshipTypes = relationshipTypeDao.getAllRelationshipTypes();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );
        RelationshipType retrievedRelationshipType1 = relationshipTypes.get( RelationshipTypeName.from( "mymodule:like" ) );
        RelationshipType retrievedRelationshipType2 = relationshipTypes.get( RelationshipTypeName.from( "mymodule:hate" ) );

        assertEquals( "mymodule:like", retrievedRelationshipType1.getName().toString() );
        assertEquals( like, retrievedRelationshipType1 );
        assertEquals( "mymodule:hate", retrievedRelationshipType2.getName().toString() );
        assertEquals( hates, retrievedRelationshipType2 );
    }
}
