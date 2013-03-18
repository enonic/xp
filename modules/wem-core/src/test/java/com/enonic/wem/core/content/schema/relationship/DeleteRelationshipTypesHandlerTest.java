package com.enonic.wem.core.content.schema.relationship;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationship.DeleteRelationshipType;
import com.enonic.wem.api.command.content.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.exception.RelationshipTypeNotFoundException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;

public class DeleteRelationshipTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteRelationshipTypeHandler handler;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        handler = new DeleteRelationshipTypeHandler();
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void deleteSingleRelationshipType()
        throws Exception
    {
        // exercise
        final QualifiedRelationshipTypeName names = QualifiedRelationshipTypeName.from( "my:relationshipType" );
        final DeleteRelationshipType command = Commands.relationshipType().delete().qualifiedName( names );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( relationshipTypeDao, times( 1 ) ).delete( isA( QualifiedRelationshipTypeName.class ), any( Session.class ) );

        final DeleteRelationshipTypeResult result = command.getResult();
        assertEquals( DeleteRelationshipTypeResult.SUCCESS, result );
    }

    @Test
    public void deleteMissingRelationshipType()
        throws Exception
    {
        // exercise
        final QualifiedRelationshipTypeName notFoundName = QualifiedRelationshipTypeName.from( "my:notFoundRelationshipType" );

        Mockito.doThrow( new RelationshipTypeNotFoundException( notFoundName ) ).
            when( relationshipTypeDao ).delete( eq( notFoundName ), any( Session.class ) );

        final DeleteRelationshipType command = Commands.relationshipType().delete().qualifiedName( notFoundName );

        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( relationshipTypeDao, times( 1 ) ).delete( isA( QualifiedRelationshipTypeName.class ), any( Session.class ) );

        final DeleteRelationshipTypeResult result = command.getResult();
        assertEquals( DeleteRelationshipTypeResult.NOT_FOUND, result );
    }

}
