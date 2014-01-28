package com.enonic.wem.core.schema.relationship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public class GetRelationshipTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetRelationshipTypeHandler handler;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        handler = new GetRelationshipTypeHandler();
        handler.setContext( this.context );
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void getRelationshipTypes()
        throws Exception
    {
        // setup
        final RelationshipTypeName name = RelationshipTypeName.from( "like" );
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            name( name ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            build();

        // expectation
        Mockito.when( relationshipTypeDao.getRelationshipType( Mockito.eq( name ) ) ).thenReturn(
            RelationshipType.newRelationshipType( relationshipType ) );

        // exercise
        final GetRelationshipType command = Commands.relationshipType().get().byName( name );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( relationshipTypeDao, only() ).getRelationshipType( Mockito.eq( name ) );
        assertNotNull( command.getResult() );
    }
}
