package com.enonic.wem.core.schema.relationship;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetAllRelationshipTypesCommandTest
{
    @Test
    public void getAllRelationshipTypes()
        throws Exception
    {
        final RelationshipTypeDao relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        final RelationshipTypeServiceImpl service = new RelationshipTypeServiceImpl();
        service.relationshipTypeDao = relationshipTypeDao;

        // setup
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            name( "mymodule-1.0.0:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            build();
        final RelationshipType relationshipType2 = RelationshipType.newRelationshipType().
            name( "mymodule-1.0.0:hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            build();

        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType, relationshipType2 );

        // expectation
        Mockito.when( relationshipTypeDao.getAllRelationshipTypes() ).thenReturn( relationshipTypes );

        // exercise
        final RelationshipTypes result = service.getAll();

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).getAllRelationshipTypes();
        assertEquals( 2, result.getSize() );
    }
}
