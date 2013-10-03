package com.enonic.wem.core.schema.relationship.dao;

import javax.jcr.Node;

import org.junit.Test;


import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeNames;
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
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "person" ) ).
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
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "person" ) ).
            build();
        relationshipTypeDao.create( like, session );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( hates, session );

        // exercise
        RelationshipTypes relationshipTypes = relationshipTypeDao.select( QualifiedRelationshipTypeNames.from( "like" ), session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 1, relationshipTypes.getSize() );
        RelationshipType createdRelationshipType = relationshipTypes.first();
        assertEquals( "like", createdRelationshipType.getName() );
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
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( like, session );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( hates, session );

        // exercise
        RelationshipTypes relationshipTypes = relationshipTypeDao.selectAll( session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );
        RelationshipType retrievedRelationshipType1 = relationshipTypes.get( QualifiedRelationshipTypeName.from( "like" ) );
        RelationshipType retrievedRelationshipType2 = relationshipTypes.get( QualifiedRelationshipTypeName.from( "hate" ) );

        assertEquals( "like", retrievedRelationshipType1.getName() );
        assertEquals( like, retrievedRelationshipType1 );
        assertEquals( "hate", retrievedRelationshipType2.getName() );
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
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( like, session );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "mymodule:thing" ) ).
            build();
        relationshipTypeDao.create( hates, session );

        // exercise
        QualifiedRelationshipTypeNames names = QualifiedRelationshipTypeNames.from( "like", "hate" );
        RelationshipTypes relationshipTypes = relationshipTypeDao.select( names, session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );
        RelationshipType retrievedRelationshipType1 = relationshipTypes.get( QualifiedRelationshipTypeName.from( "like" ) );
        RelationshipType retrievedRelationshipType2 = relationshipTypes.get( QualifiedRelationshipTypeName.from( "hate" ) );

        assertEquals( "like", retrievedRelationshipType1.getName() );
        assertEquals( like, retrievedRelationshipType1 );
        assertEquals( "hate", retrievedRelationshipType2.getName() );
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
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( like, session );

        // exercise
        QualifiedRelationshipTypeNames name = QualifiedRelationshipTypeNames.from( "like" );
        RelationshipTypes relationshipTypesAfterCreate = relationshipTypeDao.select( name, session );
        assertNotNull( relationshipTypesAfterCreate );
        assertEquals( 1, relationshipTypesAfterCreate.getSize() );

        RelationshipType relationshipTypeUpdate = newRelationshipType( like ).
            fromSemantic( "accepts" ).
            toSemantic( "accepted by" ).
            setAllowedFromTypes( QualifiedContentTypeNames.from( "worker" ) ).
            setAllowedToTypes( QualifiedContentTypeNames.from( "task" ) ).
            build();
        relationshipTypeDao.update( relationshipTypeUpdate, session );
        commit();

        // verify
        RelationshipTypes relationshipTypesAfterUpdate =
            relationshipTypeDao.select( QualifiedRelationshipTypeNames.from( "like" ), session );
        assertNotNull( relationshipTypesAfterUpdate );
        assertEquals( 1, relationshipTypesAfterUpdate.getSize() );
        RelationshipType relationshipType1 = relationshipTypesAfterUpdate.first();
        assertEquals( "like", relationshipType1.getName() );
        assertEquals( "accepts", relationshipType1.getFromSemantic() );
        assertEquals( "accepted by", relationshipType1.getToSemantic() );
        assertEquals( QualifiedContentTypeNames.from( "worker" ), relationshipType1.getAllowedFromTypes() );
        assertEquals( QualifiedContentTypeNames.from( "task" ), relationshipType1.getAllowedToTypes() );
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
            addAllowedFromType( QualifiedContentTypeName.from( "person" ) ).
            addAllowedToType( QualifiedContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.create( like, session );

        // exercise
        QualifiedRelationshipTypeNames name = QualifiedRelationshipTypeNames.from( "like" );
        RelationshipTypes relationshipTypesAfterCreate = relationshipTypeDao.select( name, session );
        assertNotNull( relationshipTypesAfterCreate );
        assertEquals( 1, relationshipTypesAfterCreate.getSize() );

        relationshipTypeDao.delete( QualifiedRelationshipTypeName.from( "like" ), session );
        commit();

        // verify
        RelationshipTypes relationshipTypesAfterDelete = relationshipTypeDao.select( name, session );
        assertNotNull( relationshipTypesAfterDelete );
        assertTrue( relationshipTypesAfterDelete.isEmpty() );
    }

}
