package com.enonic.wem.core.schema.relationship;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetAllRelationshipTypesCommandTest
    extends AbstractCommandHandlerTest
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
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            build();
        final RelationshipType relationshipType2 = RelationshipType.newRelationshipType().
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
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
