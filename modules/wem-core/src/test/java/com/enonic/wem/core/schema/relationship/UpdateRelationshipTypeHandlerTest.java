package com.enonic.wem.core.schema.relationship;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipType;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class UpdateRelationshipTypeHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateRelationshipTypeHandler handler;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void before()
        throws Exception
    {
        DateTimeUtils.setCurrentMillisFixed( new DateTime( 2012, 1, 1, 12, 0, 0 ).getMillis() );

        super.client = Mockito.mock( Client.class );
        super.initialize();

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        handler = new UpdateRelationshipTypeHandler();
        handler.setContext( this.context );
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
        RelationshipType.Builder relationshipType = RelationshipType.newRelationshipType().
            name( "like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "person" ) ).
            addAllowedToType( ContentTypeName.from( "person" ) ).
            createdTime( DateTime.now() ).
            modifiedTime( DateTime.now() );
        Mockito.when( relationshipTypeDao.getRelationshipType( isA( RelationshipTypeName.class ) ) ).thenReturn( relationshipType );
        Mockito.when( client.execute( isA( GetRelationshipType.class ) ) ).thenReturn( relationshipType.build() );

        // exercise
        UpdateRelationshipType command = Commands.relationshipType().update().
            name( RelationshipTypeName.from( "like" ) ).
            editor( new RelationshipTypeEditor()
            {
                @Override
                public RelationshipType edit( RelationshipType relationshipType )
                {
                    return RelationshipType.newRelationshipType( relationshipType ).
                        fromSemantic( relationshipType.getFromSemantic() + "-updated" ).
                        build();
                }
            } );
        this.handler.setCommand( command );
        this.handler.handle();

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).updateRelationshipType( Mockito.isA( RelationshipType.class ) );
    }

}
