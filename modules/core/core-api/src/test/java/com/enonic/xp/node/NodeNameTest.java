package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.junit.jupiter.api.Assertions.*;

public class NodeNameTest
{
    @Test
    public void testName()
        throws Exception
    {
        assertEquals( "name", NodeName.from( "name" ).toString() );
    }

    @Test
    public void testBigName()
        throws Exception
    {
        NodeName.from( "Name" );
    }

    @Test
    public void testAllowedSymbols()
        throws Exception
    {
        assertEquals( "your_name.is-okay", NodeName.from( "your_name.is-okay" ).toString() );
    }

    @Test
    public void testAsteriskName()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> NodeName.from( "name*value" ));
    }

    @Test
    public void testUnderscoreOnlyNotAllowed()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> NodeName.from( "_" ));
    }

    @Test
    public void testNameCouldStartWithUnderscore()
        throws Exception
    {
        NodeName.from( "_mystuff" );
    }

    @Test
    public void start_with_number()
        throws Exception
    {
        NodeName.from( "1myname" );
    }

    @Test
    public void equals()
    {
        final AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return NodeName.from( "name" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{NodeName.from( "other" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return NodeName.from( "name" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return NodeName.from( "name" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void testRoot()
    {
        final NodeName name = NodeName.ROOT;
        assertEquals( true, name.isRoot() );
        assertEquals( "", name.toString() );
    }

    @Test
    public void testNonRoot()
    {
        final NodeName name = NodeName.from( "test" );
        assertEquals( false, name.isRoot() );
        assertEquals( "test", name.toString() );
    }
}
