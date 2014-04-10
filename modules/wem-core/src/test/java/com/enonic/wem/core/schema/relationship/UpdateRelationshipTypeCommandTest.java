package com.enonic.wem.core.schema.relationship;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.UpdateRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class UpdateRelationshipTypeCommandTest
{
    private UpdateRelationshipTypeCommand command;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void before()
        throws Exception
    {
        DateTimeUtils.setCurrentMillisFixed( new DateTime( 2012, 1, 1, 12, 0, 0 ).getMillis() );

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        command = new UpdateRelationshipTypeCommand();
        command.relationshipTypeDao( relationshipTypeDao );
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

        // exercise
        final UpdateRelationshipTypeParams params = new UpdateRelationshipTypeParams().
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
        this.command.params( params );
        this.command.execute();

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).updateRelationshipType( Mockito.isA( RelationshipType.class ) );
    }

}
