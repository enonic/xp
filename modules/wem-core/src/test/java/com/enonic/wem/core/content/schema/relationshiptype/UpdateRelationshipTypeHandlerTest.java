package com.enonic.wem.core.content.schema.relationshiptype;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.relationshiptype.UpdateRelationshipType;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.schema.relationshiptype.editor.RelationshipTypeEditor;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.relationshiptype.dao.RelationshipTypeDao;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class UpdateRelationshipTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateRelationshipTypesHandler handler;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void before()
        throws Exception
    {
        DateTimeUtils.setCurrentMillisFixed( new DateTime( 2012, 1, 1, 12, 0, 0 ).getMillis() );
        super.initialize();

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        handler = new UpdateRelationshipTypesHandler();
        handler.setRelationshipTypeDao( relationshipTypeDao );
    }

    @After
    public void after()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void updateRelationshipType()
        throws Exception
    {
        // setup
        RelationshipType relationshipType = RelationshipType.newRelationshipType().
            module( ModuleName.from( "myModule" ) ).
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( new QualifiedContentTypeName( "myModule:person" ) ).
            addAllowedToType( new QualifiedContentTypeName( "myModule:person" ) ).
            createdTime( DateTime.now() ).
            modifiedTime( DateTime.now() ).
            build();
        Mockito.when( relationshipTypeDao.select( isA( QualifiedRelationshipTypeName.class ), any( Session.class ) ) ).thenReturn(
            relationshipType );

        // exercise
        UpdateRelationshipType command = Commands.relationshipType().update().
            selector( QualifiedRelationshipTypeName.from( "myModule:like" ) ).
            editor( new RelationshipTypeEditor()
            {
                @Override
                public RelationshipType edit( RelationshipType relationshipType )
                    throws Exception
                {
                    return RelationshipType.newRelationshipType( relationshipType ).
                        fromSemantic( relationshipType.getFromSemantic() + "-updated" ).
                        build();
                }
            } );
        this.handler.handle( this.context, command );

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).update( Mockito.isA( RelationshipType.class ), Mockito.any( Session.class ) );
    }

}
