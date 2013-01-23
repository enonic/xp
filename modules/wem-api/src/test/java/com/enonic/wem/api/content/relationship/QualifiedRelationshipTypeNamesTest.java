package com.enonic.wem.api.content.relationship;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

public class QualifiedRelationshipTypeNamesTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return QualifiedRelationshipTypeNames.from( "myModule:myRelation" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{QualifiedRelationshipTypeNames.from( "myOtherModule:myRelation" ),
                    QualifiedRelationshipTypeNames.from( "myModule:myOtherRelation" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return QualifiedRelationshipTypeNames.from( "myModule:myRelation" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return QualifiedRelationshipTypeNames.from( "myModule:myRelation" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
