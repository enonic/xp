package com.enonic.wem.api.schema.relationship;


import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;

public class RelationshipTypeNamesTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return RelationshipTypeNames.from( "mymodule:my_relation" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{RelationshipTypeNames.from( "mymodule:my_other_relation" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RelationshipTypeNames.from( "mymodule:my_relation" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RelationshipTypeNames.from( "mymodule:my_relation" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }
}
