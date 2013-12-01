package com.enonic.wem.core.schema.relationship.dao;

import javax.jcr.Node;

import org.junit.Test;


import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.AbstractJcrTest;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;

public class RelationshipTypeDaoImplTest
    extends AbstractJcrTest
{
    private RelationshipTypeDao relationshipTypeDao;

    public void setupDao()
        throws Exception
    {
        relationshipTypeDao = new RelationshipTypeDaoImpl();
    }

    @Test
    public void createRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            build();

        // exercise
        relationshipTypeDao.create( like, session );
        commit();

        // verify
        Node relationshipTypeNode = session.getNode( "/" + RelationshipTypeDao.RELATIONSHIP_TYPES_PATH + "like" );
        assertNotNull( relationshipTypeNode );
    }

    @Test
    public void selectRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            build();
        relationshipTypeDao.create( like, session );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( hates, session );

        // exercise
        RelationshipTypes relationshipTypes = relationshipTypeDao.select( RelationshipTypeNames.from( "like" ), session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 1, relationshipTypes.getSize() );
        RelationshipType createdRelationshipType = relationshipTypes.first();
        assertEquals( "like", createdRelationshipType.getName().toString() );
        assertEquals( like, createdRelationshipType );
    }

    @Test
    public void selectAllRelationshipTypes()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( like, session );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( hates, session );

        // exercise
        RelationshipTypes relationshipTypes = relationshipTypeDao.selectAll( session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );
        RelationshipType retrievedRelationshipType1 = relationshipTypes.get( RelationshipTypeName.from( "like" ) );
        RelationshipType retrievedRelationshipType2 = relationshipTypes.get( RelationshipTypeName.from( "hate" ) );

        assertEquals( "like", retrievedRelationshipType1.getName().toString() );
        assertEquals( like, retrievedRelationshipType1 );
        assertEquals( "hate", retrievedRelationshipType2.getName().toString() );
        assertEquals( hates, retrievedRelationshipType2 );
    }

    @Test
    public void selectRelationshipTypesByName()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( like, session );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( hates, session );

        // exercise
        RelationshipTypeNames names = RelationshipTypeNames.from( "like", "hate" );
        RelationshipTypes relationshipTypes = relationshipTypeDao.select( names, session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );
        RelationshipType retrievedRelationshipType1 = relationshipTypes.get( RelationshipTypeName.from( "like" ) );
        RelationshipType retrievedRelationshipType2 = relationshipTypes.get( RelationshipTypeName.from( "hate" ) );

        assertEquals( "like", retrievedRelationshipType1.getName().toString() );
        assertEquals( like, retrievedRelationshipType1 );
        assertEquals( "hate", retrievedRelationshipType2.getName().toString() );
        assertEquals( hates, retrievedRelationshipType2 );
    }

    @Test
    public void updateRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( like, session );

        // exercise
        RelationshipTypeNames name = RelationshipTypeNames.from( "like" );
        RelationshipTypes relationshipTypesAfterCreate = relationshipTypeDao.select( name, session );
        assertNotNull( relationshipTypesAfterCreate );
        assertEquals( 1, relationshipTypesAfterCreate.getSize() );

        RelationshipType relationshipTypeUpdate = newRelationshipType( like ).
            fromSemantic( "accepts" ).
            toSemantic( "accepted by" ).
            setAllowedFromTypes( ContentTypeNames.from( "worker" ) ).
            setAllowedToTypes( ContentTypeNames.from( "task" ) ).
            build();
        relationshipTypeDao.update( relationshipTypeUpdate, session );
        commit();

        // verify
        RelationshipTypes relationshipTypesAfterUpdate =
            relationshipTypeDao.select( RelationshipTypeNames.from( "like" ), session );
        assertNotNull( relationshipTypesAfterUpdate );
        assertEquals( 1, relationshipTypesAfterUpdate.getSize() );
        RelationshipType relationshipType1 = relationshipTypesAfterUpdate.first();
        assertEquals( "like", relationshipType1.getName().toString() );
        assertEquals( "accepts", relationshipType1.getFromSemantic() );
        assertEquals( "accepted by", relationshipType1.getToSemantic() );
        assertEquals( ContentTypeNames.from( "worker" ), relationshipType1.getAllowedFromTypes() );
        assertEquals( ContentTypeNames.from( "task" ), relationshipType1.getAllowedToTypes() );
    }

    @Test
    public void deleteRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( like, session );

        // exercise
        RelationshipTypeNames name = RelationshipTypeNames.from( "like" );
        RelationshipTypes relationshipTypesAfterCreate = relationshipTypeDao.select( name, session );
        assertNotNull( relationshipTypesAfterCreate );
        assertEquals( 1, relationshipTypesAfterCreate.getSize() );

        relationshipTypeDao.delete( RelationshipTypeName.from( "like" ), session );
        commit();

        // verify
        RelationshipTypes relationshipTypesAfterDelete = relationshipTypeDao.select( name, session );
        assertNotNull( relationshipTypesAfterDelete );
        assertTrue( relationshipTypesAfterDelete.isEmpty() );
    }

}
