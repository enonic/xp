package com.enonic.xp.data;


import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                return PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ),
                    PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" + ELEMENT_DIVIDER + "d" ),
                    PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "b" ),
                    PropertyPath.from( ELEMENT_DIVIDER + "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" );
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
        assertTrue( PropertyPath.from( ELEMENT_DIVIDER + "a" ).startsWith( PropertyPath.from( ELEMENT_DIVIDER + "a" ) ) );
        assertTrue( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ).startsWith( PropertyPath.from( "a" ) ) );
        assertTrue( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ).startsWith( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ) ) );
        assertTrue( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" ).startsWith( PropertyPath.from( "a" ) ) );
        assertTrue( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" ).startsWith(
            PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ) ) );
        assertTrue( PropertyPath.from( "a[1]" ).startsWith( PropertyPath.from( "a[1]" ) ) );
        assertTrue( PropertyPath.from( "a[1]" + ELEMENT_DIVIDER + "b" ).startsWith( PropertyPath.from( "a[1]" + ELEMENT_DIVIDER + "b" ) ) );
        assertTrue( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b[1]" ).startsWith( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b[1]" ) ) );

        assertFalse( PropertyPath.from( "" + ELEMENT_DIVIDER + "a" ).startsWith( PropertyPath.from( "a" ) ) );
        assertFalse( PropertyPath.from( "a" ).startsWith( PropertyPath.from( "" + ELEMENT_DIVIDER + "a" ) ) );
        assertFalse( PropertyPath.from( "a" ).startsWith( PropertyPath.from( "b" ) ) );
        assertFalse( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ).startsWith( PropertyPath.from( "a" + ELEMENT_DIVIDER + "c" ) ) );
        assertFalse( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ).startsWith(
            PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" ) ) );
        assertFalse( PropertyPath.from( "a[1]" ).startsWith( PropertyPath.from( "a[2]" ) ) );
        assertFalse( PropertyPath.from( "a[1]" + ELEMENT_DIVIDER + "b" ).startsWith( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ) ) );
        assertFalse( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" ).startsWith( PropertyPath.from( "a" + ELEMENT_DIVIDER + "b[1]" ) ) );
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
        assertEquals( "a" + ELEMENT_DIVIDER + "b", PropertyPath.from( "a[0]" + ELEMENT_DIVIDER + "b[0]" ).toString() );
    }

    @Test
    public void resetAllIndexesTo()
    {
        assertEquals( "a[5]" + ELEMENT_DIVIDER + "b[5]" + ELEMENT_DIVIDER + "c[5]" + ELEMENT_DIVIDER + "d[5]", PropertyPath.from(
            "a" + ELEMENT_DIVIDER + "b[1]" + ELEMENT_DIVIDER + "c[2]" + ELEMENT_DIVIDER + "d[3]" ).resetAllIndexesTo( 5 ).toString() );
    }


    @Test
    public void build_with_strings()
    {
        assertEquals( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c" + ELEMENT_DIVIDER + "d" + ELEMENT_DIVIDER + "e",
                      PropertyPath.from( "a" + ELEMENT_DIVIDER + "b" + ELEMENT_DIVIDER + "c", "d", "e" ).toString() );
    }

}
