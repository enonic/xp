package com.enonic.wem.api.data;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

import static junit.framework.Assert.assertEquals;

public class DataIdTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return DataId.from( "myEntry", 0 );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{DataId.from( "myEntry", 1 ), DataId.from( "myOtherEntry", 0 )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return DataId.from( "myEntry", 0 );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return DataId.from( "myEntry", 0 );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void getIndex()
    {
        assertEquals( 0, DataId.from( "myEntry", 0 ).getIndex() );
        assertEquals( 1, DataId.from( "myEntry", 1 ).getIndex() );
    }

    @Test
    public void getName()
    {
        assertEquals( "myEntry", DataId.from( "myEntry", 0 ).getName() );
    }

    @Test
    public void _toString()
    {
        assertEquals( "myEntry", DataId.from( "myEntry", 0 ).toString() );
        assertEquals( "myEntry[1]", DataId.from( "myEntry", 1 ).toString() );
        assertEquals( "myEntry[2]", DataId.from( "myEntry", 2 ).toString() );
    }

    @Test
    public void fromString()
    {
        assertEquals( "abc", DataId.from( "abc" ).toString() );
        assertEquals( "abc[3]", DataId.from( "abc[3]" ).toString() );
        assertEquals( "a[1]", DataId.from( "a[1]" ).toString() );

        assertEquals( "a", DataId.from( "a[0]" ).toString() );
        assertEquals( "abc", DataId.from( "abc[0]" ).toString() );
    }
}
