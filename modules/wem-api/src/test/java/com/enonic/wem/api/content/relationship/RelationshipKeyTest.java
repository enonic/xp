package com.enonic.wem.api.content.relationship;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

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
                return RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( DataPath.from( "myInput" ) ).
                    build();
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.LINK ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( DataPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "333" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( DataPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "444" ) ).
                    managingData( DataPath.from( "myInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( DataPath.from( "myOtherInput" ) ).
                    build(), RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    build()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( DataPath.from( "myInput" ) ).
                    build();
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RelationshipKey.newRelationshipKey().
                    type( QualifiedRelationshipTypeName.PARENT ).
                    fromContent( ContentId.from( "111" ) ).
                    toContent( ContentId.from( "222" ) ).
                    managingData( DataPath.from( "myInput" ) ).
                    build();
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

}
