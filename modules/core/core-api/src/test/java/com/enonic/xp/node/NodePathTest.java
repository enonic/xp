package com.enonic.xp.node;


import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.support.AbstractEqualsTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodePathTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new NodePath( "/myPath/myItem" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new NodePath( "/myPath" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new NodePath( "/myPath/myItem" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new NodePath( "/myPath/myItem" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void isEmpty()
    {
        assertFalse( new NodePath( "first" ).isEmpty() );
        assertTrue( new NodePath( "" ).isEmpty() );
    }

    @Test
    public void isRoot()
    {
        assertTrue( new NodePath( "/" ).isRoot() );
    }

    @Test
    public void isAbsolute()
    {
        assertTrue( new NodePath( "/first" ).isAbsolute() );
        assertFalse( new NodePath( "first" ).isAbsolute() );
    }

    @Test
    public void hasTrailingDivider()
    {
        assertTrue( new NodePath( "first/" ).hasTrailingDivider() );
        assertFalse( new NodePath( "first" ).hasTrailingDivider() );
        assertFalse( new NodePath( "/" ).hasTrailingDivider() );
    }

    @Test
    public void trimTrailingDivider()
    {
        assertEquals( "first", new NodePath( "first/" ).trimTrailingDivider().toString() );
    }

    @Test
    public void tostring()
    {
        assertEquals( "", new NodePath( "" ).toString() );
        assertEquals( "/", new NodePath( "/" ).toString() );
        assertEquals( "first", new NodePath( "first" ).toString() );
        assertEquals( "/first", new NodePath( "/first" ).toString() );
        assertEquals( "first/", new NodePath( "first/" ).toString() );
        assertEquals( "first/second", new NodePath( "first/second" ).toString() );
        assertEquals( "/first/second", new NodePath( "/first/second" ).toString() );
        assertEquals( "/first/second/", new NodePath( "/first/second/" ).toString() );
        assertEquals( "first/second/", new NodePath( "first/second/" ).toString() );
    }

    @Test
    public void ROOT()
    {
        assertEquals( "/", NodePath.ROOT.toString() );
        assertTrue( NodePath.ROOT.isAbsolute() );
        assertFalse( NodePath.ROOT.isRelative() );
        assertTrue( NodePath.ROOT.isEmpty() );
        assertFalse( NodePath.ROOT.hasTrailingDivider() );
    }

    @Test
    public void getParentPaths()
    {
        NodePath nodePath = new NodePath( "/one/two/three" );
        List<NodePath> parentPaths = nodePath.getParentPaths();
        assertEquals( 3, parentPaths.size() );
        assertEquals( "/one/two", parentPaths.get( 0 ).toString() );
        assertEquals( "/one", parentPaths.get( 1 ).toString() );
        assertEquals( "/", parentPaths.get( 2 ).toString() );
    }

    @Test
    public void removeFromBeginning()
    {
        assertEquals( "/three/four", new NodePath( "/one/two/three/four" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/three", new NodePath( "/one/two/three" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/", new NodePath( "/one/two" ).removeFromBeginning( new NodePath( "/one/two" ) ).toString() );
        assertEquals( "/", new NodePath( "/one" ).removeFromBeginning( new NodePath( "/one" ) ).toString() );
    }

    @Test
    public void removeFromBeginning_throws_IllegalStateException()
    {
        assertThrows(IllegalStateException.class, () -> assertEquals( "/", new NodePath( "/" ).removeFromBeginning( new NodePath( "/one" ) ).toString() ));
    }

    @Test
    public void compareTo()
    {
        NodePath a = new NodePath( "/a" );
        NodePath b = new NodePath( "/b" );
        NodePath arel = new NodePath( "a" );
        NodePath brel = new NodePath( "b" );
        NodePath atrail = new NodePath( "/a/" );
        NodePath btrail = new NodePath( "/b/" );

        assertThat( a.compareTo( b ) ).isLessThanOrEqualTo( -1 );
        assertThat( b.compareTo( a ) ).isGreaterThanOrEqualTo( 1 );

        assertThat( arel.compareTo( brel ) ).isLessThanOrEqualTo( -1 );
        assertThat( brel.compareTo( arel ) ).isGreaterThanOrEqualTo( 1 );

        assertThat( atrail.compareTo( btrail ) ).isLessThanOrEqualTo( -1 );
        assertThat( btrail.compareTo( atrail ) ).isGreaterThanOrEqualTo( 1 );
    }

}
