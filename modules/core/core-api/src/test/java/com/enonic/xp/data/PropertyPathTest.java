package com.enonic.xp.data;


import org.junit.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.Assert.*;

public class PropertyPathTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return PropertyPath.from( "a.b.c" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{PropertyPath.from( "a.b" ), PropertyPath.from( "a.b.c.d" ), PropertyPath.from( "a.b.b" ),
                    PropertyPath.from( ".a.b.c" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return PropertyPath.from( "a.b.c" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return PropertyPath.from( "a.b.c" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void root_is_absolute()
    {
        assertEquals( false, PropertyPath.ROOT.isRelative() );
    }

    @Test
    public void startsWith()
    {
        assertTrue( PropertyPath.from( "a" ).startsWith( PropertyPath.from( "a" ) ) );
        assertTrue( PropertyPath.from( ".a" ).startsWith( PropertyPath.from( ".a" ) ) );
        assertTrue( PropertyPath.from( "a.b" ).startsWith( PropertyPath.from( "a" ) ) );
        assertTrue( PropertyPath.from( "a.b" ).startsWith( PropertyPath.from( "a.b" ) ) );
        assertTrue( PropertyPath.from( "a.b.c" ).startsWith( PropertyPath.from( "a" ) ) );
        assertTrue( PropertyPath.from( "a.b.c" ).startsWith( PropertyPath.from( "a.b" ) ) );
        assertTrue( PropertyPath.from( "a[1]" ).startsWith( PropertyPath.from( "a[1]" ) ) );
        assertTrue( PropertyPath.from( "a[1].b" ).startsWith( PropertyPath.from( "a[1].b" ) ) );
        assertTrue( PropertyPath.from( "a.b[1]" ).startsWith( PropertyPath.from( "a.b[1]" ) ) );

        assertFalse( PropertyPath.from( ".a" ).startsWith( PropertyPath.from( "a" ) ) );
        assertFalse( PropertyPath.from( "a" ).startsWith( PropertyPath.from( ".a" ) ) );
        assertFalse( PropertyPath.from( "a" ).startsWith( PropertyPath.from( "b" ) ) );
        assertFalse( PropertyPath.from( "a.b" ).startsWith( PropertyPath.from( "a.c" ) ) );
        assertFalse( PropertyPath.from( "a.b" ).startsWith( PropertyPath.from( "a.b.c" ) ) );
        assertFalse( PropertyPath.from( "a[1]" ).startsWith( PropertyPath.from( "a[2]" ) ) );
        assertFalse( PropertyPath.from( "a[1].b" ).startsWith( PropertyPath.from( "a.b" ) ) );
        assertFalse( PropertyPath.from( "a.b" ).startsWith( PropertyPath.from( "a.b[1]" ) ) );
    }

    @Test
    public void given_a_path_with_one_element_without_index_specified_when_getIndex_of_firstElement_then_index_is_zero()
    {
        PropertyPath path = PropertyPath.from( "a" );
        assertEquals( 0, path.getFirstElement().getIndex() );
    }

    @Test
    public void given_path_with_zero_indexes_explicitly_set_then_toString_returns_path_with_implicit_zero_indexes()
    {
        assertEquals( "a.b", PropertyPath.from( "a[0].b[0]" ).toString() );
    }

    @Test
    public void resetAllIndexesTo()
    {
        assertEquals( "a[5].b[5].c[5].d[5]", PropertyPath.from( "a.b[1].c[2].d[3]" ).resetAllIndexesTo( 5 ).toString() );
    }


    @Test
    public void build_with_strings()
    {
        assertEquals( "a.b.c.d.e", PropertyPath.from( "a.b.c", "d", "e" ).toString() );
    }

}