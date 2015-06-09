package com.enonic.xp.region;

import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

public class RegionPathTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return RegionPath.from( "a-region/0/1" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {

                return new Object[]{RegionPath.from( "a-region/1/0" ), RegionPath.from( "a-region/0/0" ), RegionPath.from( "a-region" ),
                    new Object()};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return RegionPath.from( "a-region/0/1" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return RegionPath.from( ComponentPath.from( "a-region/0" ), "1" );
            }
        };

        equalsTest.assertEqualsAndHashCodeContract();
    }
}
