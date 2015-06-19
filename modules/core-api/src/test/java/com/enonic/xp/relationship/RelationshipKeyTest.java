package com.enonic.xp.relationship;


import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.support.AbstractEqualsTest;

public class RelationshipKeyTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return RelationshipKey.create().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{RelationshipKey.create().
                    type( RelationshipTypeName.REFERENCE ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build(), RelationshipKey.create().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "333" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build(), RelationshipKey.create().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "444" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build(), RelationshipKey.create().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myOtherInput" ) ).
                    build(), RelationshipKey.create().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RelationshipKey.create().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RelationshipKey.create().
                    type( RelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( PropertyPath.from( "myInput" ) ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

}
