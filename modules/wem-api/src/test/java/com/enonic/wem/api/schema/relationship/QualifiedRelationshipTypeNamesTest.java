package com.enonic.wem.api.schema.relationship;


import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;

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
                return QualifiedRelationshipTypeNames.from( "mymodule:my_relation" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{QualifiedRelationshipTypeNames.from( "myothermodule:my_relation" ),
                    QualifiedRelationshipTypeNames.from( "mymodule:my_other_relation" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return QualifiedRelationshipTypeNames.from( "mymodule:my_relation" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return QualifiedRelationshipTypeNames.from( "mymodule:my_relation" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
