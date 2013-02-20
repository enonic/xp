package com.enonic.wem.core.content.schema.relationshiptype;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Iterables;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationshiptype.DeleteRelationshipTypes;
import com.enonic.wem.api.command.content.schema.relationshiptype.RelationshipTypeDeletionResult;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.relationshiptype.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

public class DeleteRelationshipTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private DeleteRelationshipTypesHandler handler;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        handler = new DeleteRelationshipTypesHandler();
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void deleteSingleRelationshipType()
        throws Exception
    {
        // exercise
        final QualifiedRelationshipTypeNames names = QualifiedRelationshipTypeNames.from( "my:relationshipType" );
        final DeleteRelationshipTypes command = Commands.relationshipType().delete().qualifiedNames( names );
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( relationshipTypeDao, only() ).delete( isA( QualifiedRelationshipTypeName.class ), any( Session.class ) );

        RelationshipTypeDeletionResult result = command.getResult();
        assertEquals( false, result.hasFailures() );
        assertEquals( 1, Iterables.size( result.successes() ) );
    }

    @Test
    public void deleteMultipleRelationshipTypes()
        throws Exception
    {
        // exercise
        final QualifiedRelationshipTypeName existingName = QualifiedRelationshipTypeName.from( "my:existingRelationshipType" );
        final QualifiedRelationshipTypeName anotherExistingName = QualifiedRelationshipTypeName.from( "my:anotherRelationshipType" );
        final QualifiedRelationshipTypeName notFoundName = QualifiedRelationshipTypeName.from( "my:notFoundRelationshipType" );

        Mockito.doThrow( new SystemException( "Unable to delete relationship type [my:notFoundRelationshipType]" ) ).
            when( relationshipTypeDao ).delete( eq( notFoundName ), any( Session.class ) );

        final QualifiedRelationshipTypeNames names = QualifiedRelationshipTypeNames.from( existingName, notFoundName, anotherExistingName );
        final DeleteRelationshipTypes command = Commands.relationshipType().delete().qualifiedNames( names );

        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( relationshipTypeDao, times( 3 ) ).delete( isA( QualifiedRelationshipTypeName.class ), any( Session.class ) );

        RelationshipTypeDeletionResult result = command.getResult();
        assertEquals( true, result.hasFailures() );
        assertEquals( 1, Iterables.size( result.failures() ) );
        assertEquals( 2, Iterables.size( result.successes() ) );
    }

}
