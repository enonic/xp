package com.enonic.wem.core.schema.relationship.dao;

import org.junit.Before;
import org.junit.Ignore;
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

@Ignore
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
            name( "mymodule-1.0.0:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
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
            name( "mymodule-1.0.0:like" ).
            displayName( "Like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            build();
        relationshipTypeDao.createRelationshipType( like );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "mymodule-1.0.0:hate" ).
            displayName( "Hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( hates );

        // exercise
        RelationshipType.Builder relationshipType = relationshipTypeDao.getRelationshipType( RelationshipTypeName.from( "system-0.0.0:like" ) );

        // verify
        assertNotNull( relationshipType );
        RelationshipType createdRelationshipType = relationshipType.build();
        assertEquals( "mymodule-1.0.0:like", createdRelationshipType.getName().toString() );
        assertEquals( like, createdRelationshipType );
    }

    @Test
    public void selectAllRelationshipTypes()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "mymodule-1.0.0:like" ).
            displayName( "Like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( like );

        RelationshipType hates = RelationshipType.newRelationshipType().
            name( "mymodule-1.0.0:hate" ).
            displayName( "Hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( hates );

        // exercise
        RelationshipTypes relationshipTypes = relationshipTypeDao.getAllRelationshipTypes();

        // verify
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );
        RelationshipType retrievedRelationshipType1 = relationshipTypes.get( RelationshipTypeName.from( "system-0.0.0:like" ) );
        RelationshipType retrievedRelationshipType2 = relationshipTypes.get( RelationshipTypeName.from( "mymodule-1.0.0:hate" ) );

        assertEquals( "mymodule-1.0.0:like", retrievedRelationshipType1.getName().toString() );
        assertEquals( like, retrievedRelationshipType1 );
        assertEquals( "mymodule-1.0.0:hate", retrievedRelationshipType2.getName().toString() );
        assertEquals( hates, retrievedRelationshipType2 );
    }

    @Test
    public void updateRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "mymodule-1.0.0:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( like );

        // exercise
        RelationshipTypeName name = RelationshipTypeName.from( "mymodule-1.0.0:system-0.0.0:like" );
        RelationshipType.Builder relationshipTypesAfterCreate = relationshipTypeDao.getRelationshipType( name );
        assertNotNull( relationshipTypesAfterCreate );

        RelationshipType relationshipTypeUpdate = newRelationshipType( like ).
            fromSemantic( "accepts" ).
            toSemantic( "accepted by" ).
            setAllowedFromTypes( ContentTypeNames.from( "mymodule-1.0.0:mymodule-1.0.0:worker" ) ).
            setAllowedToTypes( ContentTypeNames.from( "mymodule-1.0.0:mymodule-1.0.0:task" ) ).
            build();
        relationshipTypeDao.updateRelationshipType( relationshipTypeUpdate );

        // verify
        RelationshipType.Builder relationshipTypesAfterUpdate =
            relationshipTypeDao.getRelationshipType( RelationshipTypeName.from( "system-0.0.0:like" ) );
        assertNotNull( relationshipTypesAfterUpdate );
        RelationshipType relationshipType1 = relationshipTypesAfterUpdate.build();
        assertEquals( "mymodule-1.0.0:like", relationshipType1.getName().toString() );
        assertEquals( "accepts", relationshipType1.getFromSemantic() );
        assertEquals( "accepted by", relationshipType1.getToSemantic() );
        assertEquals( ContentTypeNames.from( "mymodule-1.0.0:mymodule-1.0.0:worker" ), relationshipType1.getAllowedFromTypes() );
        assertEquals( ContentTypeNames.from( "mymodule-1.0.0:mymodule-1.0.0:task" ), relationshipType1.getAllowedToTypes() );
    }

    @Test
    public void deleteRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType like = RelationshipType.newRelationshipType().
            name( "mymodule-1.0.0:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:thing" ) ).
            build();
        relationshipTypeDao.createRelationshipType( like );

        // exercise
        RelationshipTypeName name = RelationshipTypeName.from( "system-0.0.0:like" );
        RelationshipType.Builder relationshipTypesAfterCreate = relationshipTypeDao.getRelationshipType( name );
        assertNotNull( relationshipTypesAfterCreate );

        relationshipTypeDao.deleteRelationshipType( RelationshipTypeName.from( "system-0.0.0:like" ) );

        // verify
        RelationshipType.Builder relationshipTypesAfterDelete = relationshipTypeDao.getRelationshipType( name );
        assertNull( relationshipTypesAfterDelete );
    }

}
