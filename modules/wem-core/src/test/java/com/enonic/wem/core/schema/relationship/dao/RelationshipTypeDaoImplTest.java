package com.enonic.wem.core.schema.relationship.dao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.config.SystemConfig;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;
import static org.junit.Assert.*;

public class RelationshipTypeDaoImplTest
{
    private RelationshipTypeDaoImpl relationshipTypeDao;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setupDao()
        throws Exception
    {
        relationshipTypeDao = new RelationshipTypeDaoImpl();

        final SystemConfig config = Mockito.mock( SystemConfig.class );
        Mockito.when( config.getRelationshiptTypesDir() ).thenReturn( folder.newFolder().toPath() );
        relationshipTypeDao.setSystemConfig( config );
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
        RelationshipType createdRelationship = relationshipTypeDao.createRelationshipType( like );

        // verify
        assertNotNull( createdRelationship );
    }

    @Test
    public void selectRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "like" ).
            displayName( "Like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            build();
        relationshipTypeDao.createRelationshipType( like );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "hate" ).
            displayName( "Hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( hates );

        // exercise
        RelationshipType.Builder relationshipType = relationshipTypeDao.getRelationshipType( RelationshipTypeName.from( "like" ) );

        // verify
        assertNotNull( relationshipType );
        RelationshipType createdRelationshipType = relationshipType.build();
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
            displayName( "Like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( like );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "hate" ).
            displayName( "Hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( hates );

        // exercise
        RelationshipTypes relationshipTypes = relationshipTypeDao.getAllRelationshipTypes();

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
        relationshipTypeDao.createRelationshipType( like );

        // exercise
        RelationshipTypeName name = RelationshipTypeName.from( "like" );
        RelationshipType.Builder relationshipTypesAfterCreate = relationshipTypeDao.getRelationshipType( name );
        assertNotNull( relationshipTypesAfterCreate );

        RelationshipType relationshipTypeUpdate = newRelationshipType( like ).
            fromSemantic( "accepts" ).
            toSemantic( "accepted by" ).
            setAllowedFromTypes( ContentTypeNames.from( "worker" ) ).
            setAllowedToTypes( ContentTypeNames.from( "task" ) ).
            build();
        relationshipTypeDao.updateRelationshipType( relationshipTypeUpdate );

        // verify
        RelationshipType.Builder relationshipTypesAfterUpdate =
            relationshipTypeDao.getRelationshipType( RelationshipTypeName.from( "like" ) );
        assertNotNull( relationshipTypesAfterUpdate );
        RelationshipType relationshipType1 = relationshipTypesAfterUpdate.build();
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
        relationshipTypeDao.createRelationshipType( like );

        // exercise
        RelationshipTypeName name = RelationshipTypeName.from( "like" );
        RelationshipType.Builder relationshipTypesAfterCreate = relationshipTypeDao.getRelationshipType( name );
        assertNotNull( relationshipTypesAfterCreate );

        relationshipTypeDao.deleteRelationshipType( RelationshipTypeName.from( "like" ) );

        // verify
        RelationshipType.Builder relationshipTypesAfterDelete = relationshipTypeDao.getRelationshipType( name );
        assertNull( relationshipTypesAfterDelete );
    }

}
