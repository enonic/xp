package com.enonic.wem.api.command.content.relationship;


import org.junit.Test;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.MockContentId;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

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
                return createRelationship( QualifiedRelationshipTypeName.LIKE, MockContentId.from( "1" ), MockContentId.from( "2" ),
                                           EntryPath.from( "myRef" ) );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{
                    createRelationship( QualifiedRelationshipTypeName.PARENT, MockContentId.from( "1" ), MockContentId.from( "2" ),
                                        EntryPath.from( "myRef" ) ),
                    createRelationship( QualifiedRelationshipTypeName.LIKE, MockContentId.from( "3" ), MockContentId.from( "2" ),
                                        EntryPath.from( "myRef" ) ),
                    createRelationship( QualifiedRelationshipTypeName.LIKE, MockContentId.from( "1" ), MockContentId.from( "3" ),
                                        EntryPath.from( "myRef" ) ),
                    createRelationship( QualifiedRelationshipTypeName.LIKE, MockContentId.from( "1" ), MockContentId.from( "2" ),
                                        EntryPath.from( "myOtherRef" ) ),
                    createRelationship( QualifiedRelationshipTypeName.LIKE, MockContentId.from( "1" ), MockContentId.from( "2" ), null )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return createRelationship( QualifiedRelationshipTypeName.LIKE, MockContentId.from( "1" ), MockContentId.from( "2" ),
                                           EntryPath.from( "myRef" ) );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return createRelationship( QualifiedRelationshipTypeName.LIKE, MockContentId.from( "1" ), MockContentId.from( "2" ),
                                           EntryPath.from( "myRef" ) );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    private CreateRelationship createRelationship( QualifiedRelationshipTypeName type, ContentId fromContent, ContentId toContent,
                                                   EntryPath managingPath )
    {
        CreateRelationship command = Commands.relationship().create();
        command.fromContent( fromContent );
        command.toContent( toContent );
        command.type( type );
        if ( managingPath != null )
        {
            command.managed( managingPath );
        }
        return command;
    }
}
