package com.enonic.wem.core.content.relationshiptype;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationship.CreateRelationshipType;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.relationshiptype.dao.RelationshipTypeDao;

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
        // setup
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:person" ) ).
            build();

        // exercise
        final CreateRelationshipType command = Commands.relationshipType().create().relationshipType( relationshipType );
        this.handler.handle( this.context, command );

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).create( Mockito.isA( RelationshipType.class ), Mockito.any( Session.class ) );
        final QualifiedRelationshipTypeName relationshipTypeName = command.getResult();
        assertNotNull( relationshipTypeName );
        assertEquals( "myModule:like", relationshipTypeName.toString() );
    }

}
