package com.enonic.wem.api.content.relationship;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.DataPath;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

public class RelationshipKeysTest
{
    private static final RelationshipKey parent_111_222_myInput = RelationshipKey.newRelationshipKey().
        type( QualifiedRelationshipTypeName.PARENT ).
        fromContent( ContentId.from( "111" ) ).
        toContent( ContentId.from( "222" ) ).
        managingData( DataPath.from( "myInput" ) ).build();


    private static final RelationshipKey parent_111_222_myOtherInput = RelationshipKey.newRelationshipKey().
        type( QualifiedRelationshipTypeName.PARENT ).
        fromContent( ContentId.from( "111" ) ).
        toContent( ContentId.from( "222" ) ).
        managingData( DataPath.from( "myOtherInput" ) ).build();

    private static final RelationshipKey parent_555_222_myInput = RelationshipKey.newRelationshipKey().
        type( QualifiedRelationshipTypeName.PARENT ).
        fromContent( ContentId.from( "555" ) ).
        toContent( ContentId.from( "222" ) ).
        managingData( DataPath.from( "myInput" ) ).build();

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
