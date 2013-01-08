package com.enonic.wem.core.content.relation.dao;

import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.content.relation.RelationshipTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.content.dao.ContentDaoConstants;
import com.enonic.wem.itest.AbstractJcrTest;

import static com.enonic.wem.api.content.relation.RelationshipType.newRelationType;
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
        final RelationshipType relationshipType = newRelationType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:person" ) ).
            build();

        // exercise
        relationshipTypeDao.createRelationshipType( relationshipType, session );
        commit();

        // verify
        Node relationshipTypeNode = session.getNode( "/" + ContentDaoConstants.RELATIONSHIP_TYPES_PATH + "myModule/like" );
        assertNotNull( relationshipTypeNode );
    }

    @Test
    public void retrieveRelationshipType()
        throws Exception
    {
        // setup
        final RelationshipType relationshipType = newRelationType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:person" ) ).
            build();
        relationshipTypeDao.createRelationshipType( relationshipType, session );

        // exercise
        final RelationshipTypes relationshipTypes =
            relationshipTypeDao.retrieveRelationshipTypes( QualifiedRelationshipTypeNames.from( "myModule:like" ), session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 1, relationshipTypes.getSize() );
        final RelationshipType createdRelationshipType = relationshipTypes.first();
        assertEquals( "like", createdRelationshipType.getName() );
        assertEquals( "myModule", createdRelationshipType.getModule().toString() );
        assertEquals( relationshipType, createdRelationshipType );
    }

    @Test
    public void retrieveAllRelationshipTypes()
        throws Exception
    {
        // setup
        final RelationshipType relationshipType1 = newRelationType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( relationshipType1, session );

        final RelationshipType relationshipType2 = newRelationType().
            module( ModuleName.from( "otherModule" ) ).
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( relationshipType2, session );

        // exercise
        final RelationshipTypes relationshipTypes = relationshipTypeDao.retrieveAllRelationshipTypes( session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );
        final RelationshipType retrievedRelationshipType1 = relationshipTypes.get( QualifiedRelationshipTypeName.from( "myModule:like" ) );
        final RelationshipType retrievedRelationshipType2 =
            relationshipTypes.get( QualifiedRelationshipTypeName.from( "otherModule:hate" ) );

        assertEquals( "like", retrievedRelationshipType1.getName() );
        assertEquals( "myModule", retrievedRelationshipType1.getModule().toString() );
        assertEquals( relationshipType1, retrievedRelationshipType1 );
        assertEquals( "hate", retrievedRelationshipType2.getName() );
        assertEquals( "otherModule", retrievedRelationshipType2.getModule().toString() );
        assertEquals( relationshipType2, retrievedRelationshipType2 );
    }

    @Test
    public void retrieveRelationshipTypesByName()
        throws Exception
    {
        // setup
        final RelationshipType relationshipType1 = newRelationType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( relationshipType1, session );

        final RelationshipType relationshipType2 = newRelationType().
            module( ModuleName.from( "otherModule" ) ).
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( relationshipType2, session );

        // exercise
        final QualifiedRelationshipTypeNames names = QualifiedRelationshipTypeNames.from( "myModule:like", "otherModule:hate" );
        final RelationshipTypes relationshipTypes = relationshipTypeDao.retrieveRelationshipTypes( names, session );
        commit();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );
        final RelationshipType retrievedRelationshipType1 = relationshipTypes.get( QualifiedRelationshipTypeName.from( "myModule:like" ) );
        final RelationshipType retrievedRelationshipType2 =
            relationshipTypes.get( QualifiedRelationshipTypeName.from( "otherModule:hate" ) );

        assertEquals( "like", retrievedRelationshipType1.getName() );
        assertEquals( "myModule", retrievedRelationshipType1.getModule().toString() );
        assertEquals( relationshipType1, retrievedRelationshipType1 );
        assertEquals( "hate", retrievedRelationshipType2.getName() );
        assertEquals( "otherModule", retrievedRelationshipType2.getModule().toString() );
        assertEquals( relationshipType2, retrievedRelationshipType2 );
    }

    @Test
    public void updateRelationshipType()
        throws Exception
    {
        // setup
        final RelationshipType relationshipType = newRelationType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( relationshipType, session );

        // exercise
        final QualifiedRelationshipTypeNames name = QualifiedRelationshipTypeNames.from( "myModule:like" );
        final RelationshipTypes relationshipTypesAfterCreate = relationshipTypeDao.retrieveRelationshipTypes( name, session );
        assertNotNull( relationshipTypesAfterCreate );
        assertEquals( 1, relationshipTypesAfterCreate.getSize() );

        final RelationshipType relationshipTypeUpdate = newRelationType( relationshipType ).
            fromSemantic( "accepts" ).
            toSemantic( "accepted by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:worker" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:task" ) ).
            build();
        relationshipTypeDao.updateRelationshipType( relationshipTypeUpdate, session );
        commit();

        // verify
        final RelationshipTypes relationshipTypesAfterUpdate =
            relationshipTypeDao.retrieveRelationshipTypes( QualifiedRelationshipTypeNames.from( "myModule:like" ), session );
        assertNotNull( relationshipTypesAfterUpdate );
        assertEquals( 1, relationshipTypesAfterUpdate.getSize() );
        final RelationshipType relationshipType1 = relationshipTypesAfterUpdate.first();
        assertEquals( "like", relationshipType1.getName() );
        assertEquals( "myModule", relationshipType1.getModule().toString() );
        assertEquals( "accepts", relationshipType1.getFromSemantic() );
        assertEquals( "accepted by", relationshipType1.getToSemantic() );
        assertEquals( QualifiedContentTypeNames.from( "myModule:worker" ), relationshipType1.getAllowedFromTypes() );
        assertEquals( QualifiedContentTypeNames.from( "myModule:task" ), relationshipType1.getAllowedToTypes() );
    }

    @Test
    public void deleteRelationshipType()
        throws Exception
    {
        // setup
        final RelationshipType relationshipType = newRelationType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( relationshipType, session );

        // exercise
        final QualifiedRelationshipTypeNames name = QualifiedRelationshipTypeNames.from( "myModule:like" );
        final RelationshipTypes relationshipTypesAfterCreate = relationshipTypeDao.retrieveRelationshipTypes( name, session );
        assertNotNull( relationshipTypesAfterCreate );
        assertEquals( 1, relationshipTypesAfterCreate.getSize() );

        relationshipTypeDao.deleteRelationshipType( QualifiedRelationshipTypeName.from( "myModule:like" ), session );
        commit();

        // verify
        final RelationshipTypes relationshipTypesAfterDelete = relationshipTypeDao.retrieveRelationshipTypes( name, session );
        assertNotNull( relationshipTypesAfterDelete );
        assertTrue( relationshipTypesAfterDelete.isEmpty() );
    }

}
