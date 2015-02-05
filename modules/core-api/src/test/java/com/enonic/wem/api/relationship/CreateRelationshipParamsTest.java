package com.enonic.wem.api.relationship;


import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.support.AbstractEqualsTest;

public class CreateRelationshipParamsTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return createRelationship( RelationshipTypeName.REFERENCE, ContentId.from( "1" ), ContentId.from( "2" ), "val1" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{createRelationship( RelationshipTypeName.PARENT, ContentId.from( "1" ), ContentId.from( "2" ), "val1" ),
                    createRelationship( RelationshipTypeName.REFERENCE, ContentId.from( "3" ), ContentId.from( "2" ), "val1" ),
                    createRelationship( RelationshipTypeName.REFERENCE, ContentId.from( "1" ), ContentId.from( "3" ), "val1" ),
                    createRelationship( RelationshipTypeName.REFERENCE, ContentId.from( "1" ), ContentId.from( "2" ), "otherValue" ),};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return createRelationship( RelationshipTypeName.REFERENCE, ContentId.from( "1" ), ContentId.from( "2" ), "val1" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return createRelationship( RelationshipTypeName.REFERENCE, ContentId.from( "1" ), ContentId.from( "2" ), "val1" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    private CreateRelationshipParams createRelationship( RelationshipTypeName type, ContentId fromContent, ContentId toContent,
                                                         String propValule )
    {
        CreateRelationshipParams command = new CreateRelationshipParams();
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
