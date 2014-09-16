package com.enonic.wem.core.schema.relationship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public class GetRelationshipTypeCommandTest
{
    private GetRelationshipTypeCommand command;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        command = new GetRelationshipTypeCommand();
        command.relationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void getRelationshipTypes()
        throws Exception
    {
        // setup
        final RelationshipTypeName name = RelationshipTypeName.from( "system-0.0.0:like" );
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            name( name ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            build();

        // expectation
        Mockito.when( relationshipTypeDao.getRelationshipType( Mockito.eq( name ) ) ).thenReturn( relationshipType );

        // exercise
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( name );

        this.command.params( params );
        final RelationshipType result = this.command.execute();

        // verify
        verify( relationshipTypeDao, only() ).getRelationshipType( Mockito.eq( name ) );
        assertNotNull( result );
    }
}
