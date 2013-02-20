package com.enonic.wem.core.content.schema.relationshiptype;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationshiptype.CreateRelationshipType;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.relationshiptype.dao.RelationshipTypeDao;

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
        command.module( ModuleName.from( "myModule" ) );
        command.fromSemantic( "likes" );
        command.toSemantic( "liked by" );
        command.allowedFromTypes( QualifiedContentTypeNames.from( "myModule:person" ) );
        command.allowedToTypes( QualifiedContentTypeNames.from( "myModule:person" ) );
        this.handler.handle( this.context, command );

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).create( Mockito.isA( RelationshipType.class ), Mockito.any( Session.class ) );
        final QualifiedRelationshipTypeName relationshipTypeName = command.getResult();
        assertNotNull( relationshipTypeName );
        assertEquals( "myModule:like", relationshipTypeName.toString() );
    }

}
