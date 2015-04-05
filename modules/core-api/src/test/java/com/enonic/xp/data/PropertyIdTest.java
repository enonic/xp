package com.enonic.xp.data;

import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

public class PropertyIdTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                final PropertyId propertyId = new PropertyId( "id" );
                return propertyId;
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                final PropertyId propertyId1 = new PropertyId( "idNotEqual1" );
                final PropertyId propertyId2 = new PropertyId( "idNotEqual2" );

                return new Object[]{propertyId1, propertyId2, new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                final PropertyId propertyId = new PropertyId( "id" );
                return propertyId;
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                final PropertyId propertyId = new PropertyId( "id" );
                return propertyId;
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();

    }
}
