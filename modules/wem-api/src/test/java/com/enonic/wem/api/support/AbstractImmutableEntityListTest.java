package com.enonic.wem.api.support;


import org.junit.Test;

import com.google.common.collect.ImmutableList;

import static junit.framework.Assert.assertEquals;

public class AbstractImmutableEntityListTest
{

    @Test
    public void isNotEmpty()
    {
        assertEquals( true, new MyList( ImmutableList.of( "a", "b", "c" ) ).isNotEmpty() );
    }

    @Test
    public void isEmpty()
    {
        assertEquals( false, new MyList( ImmutableList.of( "a", "b", "c" ) ).isEmpty() );
    }

    @Test
    public void get()
    {
        MyList list = new MyList( ImmutableList.of( "a", "b", "c" ) );

        assertEquals( "a", list.get( 0 ) );
        assertEquals( "b", list.get( 1 ) );
        assertEquals( "c", list.get( 2 ) );
    }

    @Test
    public void first()
    {
        MyList list = new MyList( ImmutableList.of( "a", "b", "c" ) );
        assertEquals( "a", list.first() );
    }

    @Test
    public void last()
    {
        MyList list = new MyList( ImmutableList.of( "a", "b", "c" ) );
        assertEquals( "c", list.last() );
    }

    private class MyList
        extends AbstractImmutableEntityList<String>
    {

        protected MyList( final ImmutableList<String> set )
        {
            super( set );
        }
    }
}
