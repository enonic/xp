package com.enonic.wem.api.command.relationship;


import org.junit.Test;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.support.AbstractEqualsTest;

public class CreateRelationshipTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return createRelationship( QualifiedRelationshipTypeName.LIKE, ContentId.from( "1" ), ContentId.from( "2" ), "val1" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{
                    createRelationship( QualifiedRelationshipTypeName.PARENT, ContentId.from( "1" ), ContentId.from( "2" ), "val1" ),
                    createRelationship( QualifiedRelationshipTypeName.LIKE, ContentId.from( "3" ), ContentId.from( "2" ), "val1" ),
                    createRelationship( QualifiedRelationshipTypeName.LIKE, ContentId.from( "1" ), ContentId.from( "3" ), "val1" ),
                    createRelationship( QualifiedRelationshipTypeName.LIKE, ContentId.from( "1" ), ContentId.from( "2" ), "otherValue" ),};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return createRelationship( QualifiedRelationshipTypeName.LIKE, ContentId.from( "1" ), ContentId.from( "2" ), "val1" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return createRelationship( QualifiedRelationshipTypeName.LIKE, ContentId.from( "1" ), ContentId.from( "2" ), "val1" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    private CreateRelationship createRelationship( QualifiedRelationshipTypeName type, ContentId fromContent, ContentId toContent,
                                                   String propValule )
    {
        CreateRelationship command = Commands.relationship().create();
        command.fromContent( fromContent );
        command.toContent( toContent );
        command.type( type );
        if ( propValule != null )
        {
            command.property( "prop", propValule );
        }
        return command;
    }
}
