package com.enonic.wem.core.schema.relationship;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.elasticsearch.common.joda.time.DateTimeUtils;
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
        DateTimeUtils.setCurrentMillisFixed( LocalDateTime.of( 2012, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ).toEpochMilli() );

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
            name( "mymodule-1.0.0:like" ).
            fromSemantic( "likes" ).
            toSemantic( "liked by" ).
            addAllowedFromType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            addAllowedToType( ContentTypeName.from( "mymodule-1.0.0:person" ) ).
            createdTime( Instant.now() ).
            modifiedTime( Instant.now() );
        Mockito.when( relationshipTypeDao.getRelationshipType( isA( RelationshipTypeName.class ) ) ).thenReturn( relationshipType );

        // exercise
        final UpdateRelationshipTypeParams params = new UpdateRelationshipTypeParams().
            name( RelationshipTypeName.from( "system-0.0.0:like" ) ).
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
