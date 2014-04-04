package com.enonic.wem.core.schema.relationship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.CreateRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class CreateRelationshipTypeCommandTest
    extends AbstractCommandHandlerTest
{
    private CreateRelationshipTypeCommand command;

    private RelationshipTypeDao relationshipTypeDao;

    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        relationshipTypeDao = Mockito.mock( RelationshipTypeDao.class );
        command = new CreateRelationshipTypeCommand();
        command.relationshipTypeDao( relationshipTypeDao );
    }

    @Test
    public void createRelationshipType()
        throws Exception
    {
        // exercise
        final CreateRelationshipTypeParams params = new CreateRelationshipTypeParams()
            .name( "like" )
            .displayName( "Like" )
            .fromSemantic( "likes" )
            .toSemantic( "liked by" )
            .allowedFromTypes( ContentTypeNames.from( "person" ) )
            .allowedToTypes( ContentTypeNames.from( "person" ) );

        this.command.params( params );
        final RelationshipTypeName relationshipTypeName = this.command.execute();

        // verify
        verify( relationshipTypeDao, atLeastOnce() ).createRelationshipType( Mockito.isA( RelationshipType.class ) );
        assertNotNull( relationshipTypeName );
        assertEquals( "like", relationshipTypeName.toString() );
    }

}
