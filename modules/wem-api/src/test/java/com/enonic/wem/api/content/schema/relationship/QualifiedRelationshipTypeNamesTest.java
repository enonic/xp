package com.enonic.wem.api.content.schema.relationship;


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
                return QualifiedRelationshipTypeNames.from( "mymodule:myRelation" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{QualifiedRelationshipTypeNames.from( "myothermodule:myRelation" ),
                    QualifiedRelationshipTypeNames.from( "mymodule:myOtherRelation" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return QualifiedRelationshipTypeNames.from( "mymodule:myRelation" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return QualifiedRelationshipTypeNames.from( "mymodule:myRelation" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
