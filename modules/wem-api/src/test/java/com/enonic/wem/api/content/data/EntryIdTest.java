package com.enonic.wem.api.content.data;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

import static junit.framework.Assert.assertEquals;

public class EntryIdTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return EntryId.from( "myEntry", 0 );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{EntryId.from( "myEntry", 1 ), EntryId.from( "myOtherEntry", 0 )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return EntryId.from( "myEntry", 0 );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return EntryId.from( "myEntry", 0 );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void getIndex()
    {
        assertEquals( 0, EntryId.from( "myEntry", 0 ).getIndex() );
        assertEquals( 1, EntryId.from( "myEntry", 1 ).getIndex() );
    }

    @Test
    public void getName()
    {
        assertEquals( "myEntry", EntryId.from( "myEntry", 0 ).getName() );
    }

    @Test
    public void _toString()
    {
        assertEquals( "myEntry", EntryId.from( "myEntry", 0 ).toString() );
        assertEquals( "myEntry[1]", EntryId.from( "myEntry", 1 ).toString() );
        assertEquals( "myEntry[2]", EntryId.from( "myEntry", 2 ).toString() );
    }
}
