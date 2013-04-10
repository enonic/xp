package com.enonic.wem.core.content.schema.relationship;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.api.content.schema.relationship.RelationshipTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class GetRelationshipTypesHandlerTest
    extends AbstractCommandHandlerTest
{
    private GetRelationshipTypesHandler handler;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        handler = new GetRelationshipTypesHandler();
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void getRelationshipType()
        throws Exception
    {
        // setup
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "mymodule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "mymodule:person" ) ).
            build();
        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType );
        Mockito.when( relationshipTypeDao.select( isA( QualifiedRelationshipTypeNames.class ), any( Session.class ) ) ).thenReturn(
            relationshipTypes );

        // exercise
        final QualifiedRelationshipTypeNames names = QualifiedRelationshipTypeNames.from( "mymodule:like" );
        final GetRelationshipTypes command = Commands.relationshipType().get().qualifiedNames( names );
        this.handler.handle( this.context, command );

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).select( Mockito.isA( QualifiedRelationshipTypeNames.class ),
                                                             Mockito.any( Session.class ) );
        assertEquals( 1, command.getResult().getSize() );
    }

    @Test
    public void getAllRelationshipTypes()
        throws Exception
    {
        // setup
        final RelationshipType relationshipType = RelationshipType.newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "mymodule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "mymodule:person" ) ).
            build();
        final RelationshipType relationshipType2 = RelationshipType.newRelationshipType().
            module( ModuleName.from( "mymodule" ) ).
            name( "hate" ).
            fromSemantic( "hates" ).
            toSemantic( "hated by" ).
            addAllowedFromType( new QualifiedContentTypeName( "mymodule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "mymodule:person" ) ).
            build();
        final RelationshipTypes relationshipTypes = RelationshipTypes.from( relationshipType, relationshipType2 );
        Mockito.when( relationshipTypeDao.selectAll( any( Session.class ) ) ).thenReturn( relationshipTypes );

        // exercise
        final GetRelationshipTypes command = Commands.relationshipType().get().all();
        this.handler.handle( this.context, command );

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).selectAll( Mockito.any( Session.class ) );
        assertEquals( 2, command.getResult().getSize() );
    }
}
