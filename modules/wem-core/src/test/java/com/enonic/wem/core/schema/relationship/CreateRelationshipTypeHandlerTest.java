package com.enonic.wem.core.schema.relationship;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.CreateRelationshipType;

import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class CreateRelationshipTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateRelationshipTypeHandler handler;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        handler = new CreateRelationshipTypeHandler();
        handler.setContext( this.context );
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void createRelationshipType()
        throws Exception
    {
        // exercise
        final CreateRelationshipType command = Commands.relationshipType().create();
        command.name( "like" );
        command.displayName( "Like" );
        command.fromSemantic( "likes" );
        command.toSemantic( "liked by" );
        command.allowedFromTypes( QualifiedContentTypeNames.from( "person" ) );
        command.allowedToTypes( QualifiedContentTypeNames.from( "person" ) );

        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).create( Mockito.isA( RelationshipType.class ), Mockito.any( Session.class ) );
        final QualifiedRelationshipTypeName relationshipTypeName = command.getResult();
        assertNotNull( relationshipTypeName );
        assertEquals( "like", relationshipTypeName.toString() );
    }

}
