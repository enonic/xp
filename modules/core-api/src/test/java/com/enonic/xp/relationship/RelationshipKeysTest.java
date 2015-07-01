package com.enonic.xp.relationship;


import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.support.AbstractEqualsTest;

public class RelationshipKeysTest
{
    private static final RelationshipKey parent_111_222_myInput = RelationshipKey.create().
        type( RelationshipTypeName.PARENT ).
        fromContent( ContentId.from( "111" ) ).
        toContent( ContentId.from( "222" ) ).
        managingData( PropertyPath.from( "myInput" ) ).build();


    private static final RelationshipKey parent_111_222_myOtherInput = RelationshipKey.create().
        type( RelationshipTypeName.PARENT ).
        fromContent( ContentId.from( "111" ) ).
        toContent( ContentId.from( "222" ) ).
        managingData( PropertyPath.from( "myOtherInput" ) ).build();

    private static final RelationshipKey parent_555_222_myInput = RelationshipKey.create().
        type( RelationshipTypeName.PARENT ).
        fromContent( ContentId.from( "555" ) ).
        toContent( ContentId.from( "222" ) ).
        managingData( PropertyPath.from( "myInput" ) ).build();

    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return RelationshipKeys.from( parent_111_222_myInput, parent_111_222_myOtherInput );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {

                return new Object[]{RelationshipKeys.from( parent_111_222_myOtherInput ), RelationshipKeys.from( parent_111_222_myInput ),
                    RelationshipKeys.from( parent_111_222_myInput, parent_555_222_myInput ),
                    RelationshipKeys.from( parent_111_222_myInput, parent_111_222_myOtherInput, parent_555_222_myInput )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RelationshipKeys.from( parent_111_222_myInput, parent_111_222_myOtherInput );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RelationshipKeys.from( parent_111_222_myInput, parent_111_222_myOtherInput );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

}
