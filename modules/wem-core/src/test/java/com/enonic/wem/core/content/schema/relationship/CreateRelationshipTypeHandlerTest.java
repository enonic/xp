package com.enonic.wem.core.content.schema.relationship;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

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
        command.module( ModuleName.from( "mymodule" ) );
        command.fromSemantic( "likes" );
        command.toSemantic( "liked by" );
        command.allowedFromTypes( QualifiedContentTypeNames.from( "mymodule:person" ) );
        command.allowedToTypes( QualifiedContentTypeNames.from( "mymodule:person" ) );
        this.handler.handle( this.context, command );

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).create( Mockito.isA( RelationshipType.class ), Mockito.any( Session.class ) );
        final QualifiedRelationshipTypeName relationshipTypeName = command.getResult();
        assertNotNull( relationshipTypeName );
        assertEquals( "mymodule:like", relationshipTypeName.toString() );
    }

}
